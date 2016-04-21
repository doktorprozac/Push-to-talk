package com.example.push_to_talk1;

public interface WifiThreadsListener {
	public void onConnectedIfServer(boolean isServer);
	public void onDisconnect();
	public void onReciveAudioData(byte[] data);
}
