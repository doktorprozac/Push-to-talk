package com.example.push_to_talk1;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;

import android.util.Log;

import com.example.push_to_talk1.Utils;

public class ServerThread implements Runnable {

	private static final String TAG = "ServerThread";

	private Thread serverThread;
	private boolean listeningEnabled = true;

	private DatagramSocket serversocket;
	private final int bufferSize;
	private boolean isConnected = false;

	private InetAddress clientAddress;
	private int clientPort;

	private final WifiThreadsListener activityCallback;

	public ServerThread(WifiThreadsListener activityCallback, int bufferSize) {
		this.activityCallback = activityCallback;
		this.bufferSize = bufferSize;
	}

	public void start() {
		serverThread = new Thread(this);
		listeningEnabled = true;
		serverThread.start();
	}

	public void stop() {
		listeningEnabled = false;
		closeSocket(serversocket);
		if (serverThread != null && serverThread.isAlive()) {
			serverThread.interrupt();
			serverThread = null;
		}

	}

	public void sendViaServerSocket(byte[] data) throws IOException {
		if (serversocket != null) {
			serversocket.send(new DatagramPacket(data, data.length,
					clientAddress, clientPort));
		}
	}

	@Override
	public void run() {
		try {
			serversocket = createSocket();
			DatagramPacket packet;
			byte[] buf = new byte[bufferSize];
			int size;

			// oczekiwanie na polaczenie od klienta
			while (listeningEnabled) {

				if (serversocket == null || serversocket.isClosed()) {
					serversocket = createSocket();
				}

				packet = new DatagramPacket(buf, buf.length);
				serversocket.receive(packet);
				size = packet.getData().length;
				if (size > 0) {
					if (!isConnected && isConnectRequest(packet.getData())) {
						byte[] connectedMsg = new byte[bufferSize];
						populateMessage(connectedMsg);
						clientAddress = packet.getAddress();
						clientPort = packet.getPort();
						packet = new DatagramPacket(connectedMsg,
								connectedMsg.length, clientAddress, clientPort);
						serversocket.send(packet);
						isConnected = true;
						activityCallback.onConnectedIfServer(true);
						Log.d(TAG, "Podlaczono klienta");

					} else {
						activityCallback.onReciveAudioData(packet.getData());
					}
				}

			}
			closeSocket(serversocket);
		} catch (IOException e) {
			Log.e(TAG, "Blad polaczenia, message" + e.getMessage());
			e.printStackTrace();
			closeSocket(serversocket);
		}

	}

	private void populateMessage(byte[] msg) { // tworzenie wiadomosci do wyslania
		for (int i = 0; i < msg.length; i++) {
			msg[i] = Rozmowa.CONNECTED_CODE;
		}
	}


	private boolean isConnectRequest(byte[] data) {
		for (byte b : data) {
			if (b == Rozmowa.CONNECT_REQUEST_CODE) {
				return true;
			}
		}
		return false;
	}

	private DatagramSocket createSocket() throws SocketException {
		DatagramSocket socket = new DatagramSocket(null);
		socket.setReuseAddress(true);
		socket.bind(new InetSocketAddress(Utils.SERVERPORT));
		return socket;
	}

	private void closeSocket(DatagramSocket socket) {
		isConnected = false;
		if (socket != null && !socket.isClosed()) {
			socket.close();
		}
		// activityCallback.onDisconnect();
	}

}
