package com.example.push_to_talk1;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.example.push_to_talk1.R;
import com.example.push_to_talk1.Utils;

import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;
import android.widget.TextView;

public class Rozmowa extends Activity implements OnTouchListener,
OnLongClickListener, OnClickListener, WifiThreadsListener {

	private ImageButton nadaj;
	private ServerThread serverThread;
	private ClientThread clientThread;
	
	static final byte CONNECT_REQUEST_CODE = 11; // wysyla klient do serwera
	static final byte CONNECTED_CODE = 22; // serwer odpowiada czy sie polaczyl
	static final int FREQUENCY = 44100;
	static final int AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
	
	private static final String TAG = "WifiActivity";
	private static final Queue<byte[]> playbackData = new ConcurrentLinkedQueue<byte[]>();
	
	private AudioTrack audiotrack;
	private AudioRecord recorder;
	private TextView status_polaczenia;
	

	private int bufferSizeRecorder;
	
	private boolean isConnected = false;
	private boolean isServer;
	private boolean isClient;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.nadawanie);
		
		nadaj = (ImageButton) findViewById(R.id.nadaj);
		status_polaczenia = (TextView) findViewById(R.id.status);
		
		serverThread = new ServerThread((WifiThreadsListener)this, bufferSizeRecorder);
		serverThread.start();

		clientThread = new ClientThread((WifiThreadsListener)this, bufferSizeRecorder);

		
	}
	private void setUiAfterSuccessConnect(boolean connected) {
		if (connected) {
			status_polaczenia.setText("Podlaczono, wcisnij przycisk ");
		} else {
			status_polaczenia.setText("Rozlaczono");
		}
	}
	
	@Override
	public void onConnectedIfServer(boolean isServer) {
		// TODO Auto-generated method stub
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				setUiAfterSuccessConnect(true);
				//startPlaybackThread();
				//playBackEnabled = true;

			}
		});
		this.isServer = isServer;
		this.isClient = !isServer;
		isConnected = true;
	}
	@Override
	public void onDisconnect() {
		// TODO Auto-generated method stub
		isConnected = false;
		//playBackEnabled = false;
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				setUiAfterSuccessConnect(false);
				//stopThread(playBackThread);
			}
		});
		//stopRecording();
		//stopPlayback();
	}
	@Override
	public void onReciveAudioData(byte[] data) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public boolean onLongClick(View v) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		return false;
	}

}
