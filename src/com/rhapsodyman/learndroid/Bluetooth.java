package com.rhapsodyman.learndroid;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

public class Bluetooth {
	private static Bluetooth instance = null;
	private Activity callerActivity = null;
	static boolean isSocketCreated = false;
	private BluetoothAdapter btAdapter;
	private static final String TAG = "bluetooth2";
	private BluetoothDevice device;
	private static UUID MY_UUID;
	private Handler mHandler;
	private String MAC_addr;
	static final int DATA_RECEIVED = 9999;
	static final int SOCKET_CONNECTED = 8888;
	static final int ERROR_EXIT = 7777;
	static final int REQUEST_ENABLE_BT = 1;
	static boolean flag = false;
	private ConnectThread thread;
	private static ConnectedThread connection;

	private Bluetooth(Activity activ, Handler handler, String MAC,
			String DEV_UUID) {
		callerActivity = activ;
		mHandler = handler;
		MAC_addr = MAC;
		MY_UUID = UUID.fromString(DEV_UUID);

		btAdapter = BluetoothAdapter.getDefaultAdapter();
		device = btAdapter.getRemoteDevice(MAC_addr);
	}

	public static Bluetooth getInstance(Activity activ, Handler handler,
			String MAC, String DEV_UUID) {
		if (instance == null) {
			instance = new Bluetooth(activ, handler, MAC, DEV_UUID);
		}
		return instance;

	}

	public static ConnectedThread getConnection() {
		return connection;
	}

	public void connect() {

		thread = new ConnectThread(device, mHandler);
		thread.start();
	}

	public void checkBTState() {
		Log.d(TAG, "...check BT state..");
		// Check for Bluetooth support and then check to make sure it is turned
		// on

		if (btAdapter == null) {
			errorExit("Fatal Error", "Bluetooth не поддерживается");
		} else {
			if (btAdapter.isEnabled()) {
				// if (thread == null ) connect();
				connect();

				Log.d(TAG, "...Bluetooth включен...");
			} else {
				// Prompt user to turn on Bluetooth
				Intent enableBtIntent = new Intent(
						BluetoothAdapter.ACTION_REQUEST_ENABLE);
				Log.d(TAG, "...starting activity for result ...");
				callerActivity.startActivityForResult(enableBtIntent,
						REQUEST_ENABLE_BT);
			}
		}
	}

	private class ConnectThread extends Thread {
		private final BluetoothSocket mmSocket;
		private final BluetoothDevice mmDevice;
		private Handler mmHandler;

		public ConnectThread(BluetoothDevice device, Handler handler) {
			isSocketCreated = false;
			BluetoothSocket tmp = null;
			mmDevice = device;
			mmHandler = handler;

			// Get a BluetoothSocket to connect with the given BluetoothDevice
			try {
				tmp = mmDevice
						.createRfcommSocketToServiceRecord(Bluetooth.MY_UUID);
			} catch (IOException e) {
			}

			mmSocket = tmp;
		}

		public void run() {
			Log.d("andrey", "try to connect");
			try {
				// Connect the device through the socket. This will block
				// until it succeeds or throws an exception
				mmSocket.connect();

			} catch (IOException connectException) {

				// Unable to connect; close the socket and get out
				try {
					Log.d("andrey", "some exception closing socket");
					mmSocket.close();
					mmHandler.obtainMessage(ERROR_EXIT, 0).sendToTarget();

				} catch (IOException closeException) {
				}
				return;
			}
			isSocketCreated = true;
			// Do work to manage the connection (in a separate thread)
			manageConnectedSocket();
		}

		private void manageConnectedSocket() {
			connection = new ConnectedThread(mmSocket, mmHandler);
			mmHandler.obtainMessage(SOCKET_CONNECTED, connection)
					.sendToTarget();
			connection.start();
		}
	}

	class ConnectedThread extends Thread {
		private final BluetoothSocket mmSocket;
		private final InputStream mmInStream;
		private final OutputStream mmOutStream;
		private final Handler mmHandler;

		public ConnectedThread(BluetoothSocket socket, Handler handler) {

			mmSocket = socket;
			mmHandler = handler;
			InputStream tmpIn = null;
			OutputStream tmpOut = null;
			Log.d("andrey", "get the i/o streams");
			// Get the input and output streams, using temp objects because
			// member streams are final
			try {
				tmpIn = socket.getInputStream();
				tmpOut = socket.getOutputStream();
			} catch (IOException e) {
			}

			Log.d("andrey", "succes");
			mmInStream = tmpIn;
			mmOutStream = tmpOut;
		}

		public void run() {
			byte[] readBuffer = new byte[256]; // buffer store for the stream
			int bytes; // bytes returned from read()
			final byte delimiter = 10; // This is the ASCII code for a newline
										// character

			int readBufferPosition = 0;

			// Keep listening to the InputStream until an exception occurs
			while (true) {

				try {
					int bytesAvailable = mmInStream.available();
					if (bytesAvailable > 0) {
						byte[] packetBytes = new byte[bytesAvailable];
						mmInStream.read(packetBytes);
						for (int i = 0; i < bytesAvailable; i++) {
							byte b = packetBytes[i];
							if (b == delimiter) {
								byte[] encodedBytes = new byte[readBufferPosition];
								System.arraycopy(readBuffer, 0, encodedBytes,
										0, encodedBytes.length - 1);
								final String data = new String(encodedBytes,
										"US-ASCII");
								readBufferPosition = 0;

								mmHandler.obtainMessage(DATA_RECEIVED, -1, -1,
										data).sendToTarget();

							} else {
								readBuffer[readBufferPosition++] = b;
							}
						}
					}

				}

				catch (IOException e) {
					break;
				}
			}
		}

		/* Call this from the main activity to send data to the remote device */
		public void write(String message) {
			byte[] bytes = message.getBytes();
			try {
				mmOutStream.write(bytes);
			} catch (IOException e) {
			}
		}

		/* Call this from the main activity to shutdown the connection */
		public void cancel() {
			try {
				mmSocket.close();
			} catch (IOException e) {
			}
		}
	}

	private void errorExit(String title, String message) {

		Toast.makeText(callerActivity, title + " - " + message,
				Toast.LENGTH_LONG).show();
		callerActivity.finish();
	}

}
