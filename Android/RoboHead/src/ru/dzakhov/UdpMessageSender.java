package ru.dzakhov;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.ArrayBlockingQueue;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;

/**
 * This class implements message sending by UDP datagrams. 
 * @author Dmitry Dzakhov
 *
 */
public final class UdpMessageSender extends Thread {
	/**
	 * Maximum capacity of the intput queue.
	 */
	private final int mQueueCapacity = 100;
	
	/**
	 * Queue for sending messages.
	 */
	private ArrayBlockingQueue<String> mQueue = new ArrayBlockingQueue<String>(mQueueCapacity);
	
	/**
	 * Main activity's context.
	 */
	private Context mContext;
	
	/**
	 * Instance constructor.
	 * @param context of the main activity.
	 */
	public UdpMessageSender(final Context context) {
		mContext = context;
	}
	
	/**
	 * Send a message through UDP socket.
	 * @param message to send.
	 */
	public synchronized void send(final String message) {
		try {
			mQueue.put(message);
		} catch (InterruptedException e) {
			Logger.e("UdpMessageSender error: " + e.getLocalizedMessage());
		}
	}
	
	/**
	 * Runnable interface implementation.
	 */
	public void run() {
		DatagramSocket datagramSocket = null;
		InetAddress inetAddress = null;
		try {
			datagramSocket = new DatagramSocket(Settings.getUdpSendPort());
			
			if (Settings.getUdpSendBroadcast()) {
				datagramSocket.setBroadcast(true);
				if (Settings.getUdpSendBroadcastLocal()) {
					inetAddress = getLocalNetworkBroadcastAddress(); 
				} else {
					inetAddress = InetAddress.getByName("255.255.255.255"); 
				}
			} else {
				datagramSocket.setBroadcast(false);
				inetAddress = InetAddress.getByName(Settings.getUdpRecipientIp());
			}
		} catch (Exception e) {
			Logger.e("UdpMessageSender error: " + e.getLocalizedMessage());
			return;
		}

		Logger.d("UdpMessageSender: started");
		Logger.d("UdpMessageSender: recipient address – " + inetAddress.getHostAddress());
			
		String message = "";
//		while (!Thread.currentThread().isInterrupted()) {
		while (true) {
			try {
				message = mQueue.take();
			} catch (InterruptedException e1) {
				break;
			}

			if ((message != null) && (message.length() > 0)) {
				DatagramPacket sendPacket = new DatagramPacket(
						message.getBytes(), 
						message.length(),
						inetAddress,
						Settings.getUdpSendPort());
				try {
					datagramSocket.send(sendPacket);
					Logger.d("UdpMessageSender has sent command: " + message);
				} catch (IOException e) {
					Logger.e("UdpMessageSender error: " + e.getLocalizedMessage());
				}
			}
		} // read while cycle

		if ((datagramSocket != null) && (!datagramSocket.isClosed())) {
			try {
				datagramSocket.close();
			} catch (Exception e) {
				Logger.e("UdpMessageSender error: " + e.getLocalizedMessage());
			}
		}
		datagramSocket = null;
		Logger.d("UdpMessageSender: stopped");
	}
	
	/**
	 * Gets the broadcast address mask inside local network.
	 * @return broadcast address mask for current local network.
	 * @throws IOException if error.
	 */
	private InetAddress getLocalNetworkBroadcastAddress() throws IOException {
		InetAddress result;
	    try {
		    WifiManager wifi = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
		    DhcpInfo dhcp = wifi.getDhcpInfo();
		    if (dhcp == null) {
		    	throw new IOException("Can't get DHCP info.");
		    }
	
		    int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
		    final char addressBytes = 4;
		    final char bitsInByte = 8;
		    final char ffMask = 0xFF;
		    byte[] quads = new byte[addressBytes];
		    for (int k = 0; k < addressBytes; k++) {
		      quads[k] = (byte) ((broadcast >> k * bitsInByte) & ffMask);
		    }		    
		    
			result = InetAddress.getByAddress(quads);
		} catch (Exception e) {
			Logger.e(e.getMessage());
			Logger.w("Trying to Broadcast through 255.255.255.255 IP-mask.");
			result = InetAddress.getByName("255.255.255.255");
		}
	    
	    return result;
	}
}
