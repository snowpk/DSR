package main;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class UDPFloodingThread extends Thread {

	private DatagramSocket socket;
	private UDPSender udpSender;
	private InetAddress ownIpAddress;
	private Set<Long> receivedMessageTimeStamp = new HashSet<>();

	public UDPFloodingThread(DatagramSocket socket, InetAddress address, InetAddress ownIpAddress)
			throws SocketException {
		this.socket = socket;
		this.udpSender = new UDPSender(5014, address);
		this.ownIpAddress = ownIpAddress;
	}

	@Override
	public void run() {
		byte[] data = new byte[1024];
		DatagramPacket packet = new DatagramPacket(data, data.length);
		System.out.println("Listening...");
		while (true) {
			try {
				socket.receive(packet);
				FloodMessage receivedMessage = encodeMessage(packet);

				// REQUEST
				if (receivedMessage.getType() == 1) {

					
					if (!receivedMessageTimeStamp.contains(receivedMessage.getTimestamp())
							&& !packet.getAddress().equals(ownIpAddress)) {
						receivedMessageTimeStamp.add(receivedMessage.getTimestamp());

						if (receivedMessage.getDestination().equals(ownIpAddress)) {

							receivedMessage.addToRoute(ownIpAddress);
							receivedMessage.setType(2);
							receivedMessage.setIndex(1);
							udpSender.sendUnicastPacket(receivedMessage.getBytes(), receivedMessage.route
									.get(receivedMessage.route.size() - (1 + receivedMessage.getIndex())));

						} else {
							receivedMessage.addToRoute(ownIpAddress);
							udpSender.sendPacket(receivedMessage.getBytes());
						}

						if (receivedMessage.getType() == 1) {
							System.out.println("Broadcasting RREQ packet for destination " + receivedMessage.getDestination().toString());
						} else {
							System.out.println("Unicasting RREP packet for destination " + receivedMessage.getSource().toString());
						}

					} else {
						System.out.println("Already saw message, do nothing");
					}
					// REPLY
				} else if (receivedMessage.getType() == 2) {

					// Not the destination of the Reply
					if (!ownIpAddress.equals(receivedMessage.source)) {
						receivedMessage.setIndex(receivedMessage.getIndex() + 1);
						udpSender.sendUnicastPacket(receivedMessage.getBytes(), receivedMessage.route
								.get(receivedMessage.route.size() - (1 + receivedMessage.getIndex())));
						System.out.println("Forwarding RREP packet for destination " + receivedMessage.getSource().toString());
						// Destination of the Reply, print the Route from Source to Dest
					} else {

						printMessageDetails(packet, receivedMessage);

					}

				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private FloodMessage encodeMessage(DatagramPacket packet) throws IOException, ClassNotFoundException {
		ByteArrayInputStream bis = new ByteArrayInputStream(packet.getData());
		ObjectInput in = new ObjectInputStream(bis);
		return (FloodMessage) in.readObject();
	}

	private void printMessageDetails(DatagramPacket packet, FloodMessage receivedMessage) {
		System.out.println("====================================================");
		System.out.println("Route from " + receivedMessage.getSource().toString() + " to "
				+ receivedMessage.getDestination().toString() + ":");
		for (InetAddress a : receivedMessage.route) {
			System.out.println(a.toString());
		}
		System.out.println(" Route Discovery took " + (new Date().getTime() - receivedMessage.getTimestamp()));
	}
}
