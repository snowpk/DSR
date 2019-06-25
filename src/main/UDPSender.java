package main;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class UDPSender {

    private int port;
    private InetAddress address;
    private DatagramSocket socket;
    private DatagramSocket unicastSocket;

    public UDPSender(int port, InetAddress address) throws SocketException {
        this.port = port;
        this.address = address;
        //Broadcast
        this.socket = new DatagramSocket();
        socket.setBroadcast(true);
        this.unicastSocket = new DatagramSocket();
        
    }

    void sendPacket(byte[] message) throws IOException {
        System.out.println("Send packet");
        DatagramPacket packet = new DatagramPacket(
                message, message.length,
                address, port);

        socket.send(packet);
    }
    
    //To send the RREP via unicast
    void sendUnicastPacket(byte[] message, InetAddress target) throws IOException {
    	System.out.println("Send packet");
        DatagramPacket packet = new DatagramPacket(
                message, message.length,
                target, port);

        unicastSocket.send(packet);
    }

}
