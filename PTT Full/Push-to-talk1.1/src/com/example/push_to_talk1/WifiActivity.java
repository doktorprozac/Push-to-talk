package com.example.push_to_talk1;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Locale;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.net.ConnectivityManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.push_to_talk1.R;
import com.example.push_to_talk1.Utils;

public class WifiActivity extends Activity implements OnTouchListener,
		OnLongClickListener, OnClickListener, WifiThreadsListener {

	static final byte CONNECT_REQUEST_CODE = 11; // wysyla klient do serwera
	static final byte CONNECTED_CODE = 22; // serwer odpowiada czy sie polaczyl
	static final int FREQUENCY = 44100;
	static final int AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
	
	private static final String TAG = "WifiActivity";
	private static final Queue<byte[]> playbackData = new ConcurrentLinkedQueue<byte[]>();
	
	private AudioTrack audiotrack;
	private AudioRecord recorder;

	private TextView adress_ip;
	private ImageButton nagraj;
	private EditText wprowadz;
	private Button polacz;
	private TextView statusTekst;

	private ServerThread serverThread;
	private ClientThread clientThread;

	private Thread recordingThread;
	private boolean recordingEnabled = false;

	private Thread playBackThread;
	private boolean playBackEnabled = false;

	private boolean isConnected = false;
	private boolean isServer;
	private boolean isClient;

	private int bufferSizeRecorder;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dzwiek2);

		nagraj = (ImageButton) findViewById(R.id.nagraj);
		nagraj.setEnabled(false);
		nagraj.setOnLongClickListener(this);
		nagraj.setOnTouchListener(this);

		polacz = (Button) findViewById(R.id.polacz);
		polacz.setOnClickListener(this);

		wprowadz = (EditText) findViewById(R.id.wprowadz);
		adress_ip = (TextView) findViewById(R.id.adress_ip);
		statusTekst = (TextView) findViewById(R.id.status);

		bufferSizeRecorder = AudioRecord.getMinBufferSize(FREQUENCY,
				AudioFormat.CHANNEL_IN_MONO, AUDIO_ENCODING);

		int maxBufferSize = 4096;
		bufferSizeRecorder = Math.max(bufferSizeRecorder, maxBufferSize); 

		audiotrack = new AudioTrack(AudioManager.STREAM_MUSIC, FREQUENCY,
				AudioFormat.CHANNEL_OUT_MONO, AUDIO_ENCODING,
				bufferSizeRecorder, AudioTrack.MODE_STREAM);

		recorder = new AudioRecord(MediaRecorder.AudioSource.MIC, FREQUENCY,
				AudioFormat.CHANNEL_IN_MONO, AUDIO_ENCODING, bufferSizeRecorder);

		serverThread = new ServerThread((WifiThreadsListener)this, bufferSizeRecorder);
		serverThread.start();

		clientThread = new ClientThread((WifiThreadsListener)this, bufferSizeRecorder);
		
		Bundle b = new Bundle();
	    b = getIntent().getExtras();
	    String name = b.getString("name");
	    
	    wprowadz.setText(name);
	}

	@Override
	protected void onStart() {
		super.onStart();
		// rejestruj zmiany sieci -> zmienia adres ip, gdy siê zmieni siec
		registerReceiver(connectivityReceiver, new IntentFilter(
				ConnectivityManager.CONNECTIVITY_ACTION));
	}

	@Override
	protected void onPause() {
		// przestan rejestrowac zmiany sieci
		unregisterReceiver(connectivityReceiver);

		// zatrzymaj wszystkie uruchomione watki
		serverThread.stop();
		clientThread.stop();

		recordingEnabled = false;
		playBackEnabled = false;

		stopThread(recordingThread);
		stopThread(playBackThread);

		stopPlayback(); // zatrzymanie odbiektu odtwarzania - audiotrack

		stopRecording(); // ----||------------- - audiorecord

		super.onPause();
	}

	private void stopThread(Thread thread) {
		if (thread != null && thread.isAlive()) {
			thread.interrupt();
			thread = null;
		}
	}

	// reciver do rejestracji zmian sieci // jak sie zmieni siec
	private BroadcastReceiver connectivityReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			getWifiIpAddress();
		}
	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.polacz:
			polacz();
			break;
		}
	}

	private void polacz() {
		String address = wprowadz.getText().toString();
		if (Utils.validateIp(address)) {
			try {
				InetAddress serverAddress = InetAddress.getByName(address);
				serverThread.stop();
				clientThread.stop();

				clientThread.start(serverAddress);

			} catch (UnknownHostException e) {
				Toast.makeText(getApplicationContext(),
						"Nieznany adres, blad polaczenia", Toast.LENGTH_LONG)
						.show();
			}
		} else {
			Toast.makeText(getApplicationContext(), "Wprowadz poprawny adres",
					Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public boolean onLongClick(View v) {
		switch (v.getId()) {
		case R.id.nagraj:
			Toast.makeText(getApplicationContext(), "Trwa rejestrowanie dŸwiêku",
					Toast.LENGTH_LONG).show();
			stopRecording();
			startRecording();
			break;
		}
		return false;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (v.getId() == R.id.nagraj) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_UP:
				stopRecording();
			}
		}
		return false;
	}

	// onConnected - informuje o tym ze polaczyl sie z telefonem, iformuje o tym ¿e serwer np. sie polaczyl
	@Override
	public void onConnectedIfServer(boolean isServer) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				setUiAfterSuccessConnect(true);
				startPlaybackThread();
				playBackEnabled = true;

			}
		});
		this.isServer = isServer;
		this.isClient = !isServer;
		isConnected = true;
	}

	@Override
	public void onDisconnect() {
		isConnected = false;
		playBackEnabled = false;
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				setUiAfterSuccessConnect(false);
				stopThread(playBackThread);
			}
		});
		stopRecording();
		stopPlayback();

	}

	@Override
	public void onReciveAudioData(byte[] data) {
		playbackData.add(data);
	}

	private void startRecording() {
		if (isConnected) {
			recordingEnabled = true;
			recordingThread = new Thread(recordingRunnable);
			recordingThread.start();
		}
	}

	private void stopRecording() {
		recordingEnabled = false;

		try {
			if (null != recorder
					&& recorder.getState() == AudioRecord.STATE_INITIALIZED) {
				recorder.stop();
				recorder.release();
				recorder = null;
			}
		} catch (IllegalStateException e) {
			Log.w(TAG, "Recording is stopped no need to stop");
		}
		stopThread(recordingThread);
	}

	private void startPlaybackThread() {
		playBackThread = new Thread(playBackRunnable);
		playBackThread.start();
	}

	private void stopPlayback() {
		try {
			if (audiotrack != null) {
				audiotrack.stop();
				audiotrack.release();
				audiotrack = null;
			}

		} catch (IllegalStateException e) {
			Log.w(TAG, "Recording is stopped no need to stop");
		}

	}

	private void setUiAfterSuccessConnect(boolean connected) {
		polacz.setEnabled(!connected);
		wprowadz.setEnabled(!connected);
		nagraj.setEnabled(connected);
		if (connected) {
			statusTekst.setText("Podlaczono, wcisnij przycisk ");
		} else {
			statusTekst.setText("Rozlaczono");
		}
	}

	private String getWifiIpAddress() {
		WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		int ip = wifiInfo.getIpAddress();

		String ipString = String.format(Locale.getDefault(), "%d.%d.%d.%d",
				(ip & 0xff), (ip >> 8 & 0xff), (ip >> 16 & 0xff),
				(ip >> 24 & 0xff));

		adress_ip.setText("Moj adres IP to: " + ipString);
		return ipString;
	}

	private Runnable recordingRunnable = new Runnable() {
		@Override
		public void run() {

			if (recorder == null) {
				recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
						FREQUENCY, AudioFormat.CHANNEL_IN_MONO, AUDIO_ENCODING,
						bufferSizeRecorder);

			}
			recorder.startRecording();
			if (recorder != null) {

				byte buffer[] = new byte[bufferSizeRecorder];
				byte[] data;
				while (recordingEnabled) {
					try {
						int bufferReadResult = recorder.read(buffer, 0,
								bufferSizeRecorder);
						if (bufferReadResult > 0) {

							data = new byte[bufferReadResult];
							for (int i = 0; i < bufferReadResult; i++) {
								data[i] = buffer[i];
							}

							if (isClient) {
								clientThread.sendViaClientSocket(data);
							} else if (isServer) {
								serverThread.sendViaServerSocket(data);
							}

						}

					} catch (IOException e) {
						Log.d(TAG, "Eror send recording");
					}
				}
				stopRecording();
			}
		}
	};

	Runnable playBackRunnable = new Runnable() {
		public void run() {
			audiotrack.setPlaybackRate(FREQUENCY);
			audiotrack.play();
			int currentSize;
			byte element[];
			while (playBackEnabled) {
				currentSize = playbackData.size();
				if (currentSize > 0) {
					element = playbackData.poll();
					audiotrack.write(element, 0, element.length);

				}
			}
			stopPlayback();
		}
	};

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		Intent lista_uzytk = new Intent(WifiActivity.this, Lista_uzytkownikow.class);
		startActivity(lista_uzytk);
		super.onBackPressed();
	}

}
