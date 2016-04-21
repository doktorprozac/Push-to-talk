package com.example.push_to_talk1;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import android.util.Log;


public class ClientThread implements Runnable {

	private static final String TAG = "ClientThread";

	private final int bufferSize;

	private DatagramSocket clientSocket;
	private boolean isConnected = false;

	private Thread clientThread;
	private boolean recivingEnabled = false;

	private InetAddress serverAddress;

	private final WifiThreadsListener activityCallback;

	public ClientThread(WifiThreadsListener activityCallback, int bufferSize) {
		this.activityCallback = activityCallback;
		this.bufferSize = bufferSize;
	}

	public void start(InetAddress serverAddress) {
		this.serverAddress = serverAddress;
		clientThread = new Thread(this);
		recivingEnabled = true;
		clientThread.start();
	}

	public void stop() {
		recivingEnabled = false;
		closeSocket(clientSocket);
		if (clientThread != null && clientThread.isAlive()) {
			clientThread.interrupt();

			clientThread = null;
		}

	}

	public void sendViaClientSocket(byte[] data) throws IOException {
		if (clientSocket != null) {
			clientSocket.send(new DatagramPacket(data, data.length,
					serverAddress, Utils.SERVERPORT));
		}

	}

	@Override
	public void run() {
		try {
			clientSocket = createSocket();
			byte[] connectMsg = new byte[bufferSize];
			populateMessage(connectMsg);
			DatagramPacket packet = new DatagramPacket(connectMsg,
					connectMsg.length, serverAddress, Utils.SERVERPORT);
			clientSocket.send(packet);

			// stworzenie bufora
			byte[] buf = new byte[bufferSize];
			int size;

			// odbieraj wiadomosci od serwera
			while (recivingEnabled) {
				packet = new DatagramPacket(buf, buf.length);
				clientSocket.receive(packet);
				size = packet.getData().length;
				if (size > 0) {
					if (!isConnected && isConnectRequest(packet.getData())) {
						isConnected = true;
						activityCallback.onConnectedIfServer(false);
					} else {
						activityCallback.onReciveAudioData(packet.getData());
					}
				}
			}
			closeSocket(clientSocket);
		} catch (IOException e) {
			Log.e(TAG, "Blad odbierania");
			e.printStackTrace();
			closeSocket(clientSocket);
		}

	}

	private boolean isConnectRequest(byte[] data) {

		for (byte b : data) {
			if (b == Rozmowa.CONNECTED_CODE) {
				return true;
			}
		}
		return false;
	}

	private void populateMessage(byte[] msg) {
		for (int i = 0; i < msg.length; i++) {
			msg[i] = Rozmowa.CONNECT_REQUEST_CODE;
		}
	}

	private DatagramSocket createSocket() throws SocketException {
		return new DatagramSocket();
	}

	private void closeSocket(DatagramSocket socket) {
		isConnected = false;
		if (socket != null && !socket.isClosed()) {
			socket.close();
		}
		// activityCallback.onDisconnect();
	}

}
