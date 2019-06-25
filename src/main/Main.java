package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.Date;
import java.util.Enumeration;

public class Main {

    public static void main(String[] args) throws IOException {
        Thread udpServer = new UDPFloodingThread(new DatagramSocket(5014), InetAddress.getByName("192.168.210.255"), getOwnIPAddress());
        udpServer.start();

        BufferedReader inputReader = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            System.out.println("Type message to flood:...");
            String userInput = inputReader.readLine();
            System.out.println("Type destination IP [Format : XXX.XXX.XXX.XXX ]:...");
            String destInput = inputReader.readLine();
            
          
            FloodMessage message = new FloodMessage(new Date().getTime(), userInput, 1, InetAddress.getByName(destInput), getOwnIPAddress());
            message.addToRoute(getOwnIPAddress());

            UDPSender sender = new UDPSender(5014, InetAddress.getByName("192.168.210.255"));
            sender.sendPacket(message.getBytes());
            
            
        }
    }

    private static Inet4Address getOwnIPAddress() throws SocketException {
        // Source: http://www.java2s.com/Tutorials/Java/Network/IP/Get_IP_address_from_NetworkInterface_in_Java.htm
        String interfaceName = "wlan0";
        NetworkInterface networkInterface = NetworkInterface.getByName(interfaceName);
        Enumeration<InetAddress> inetAddress = networkInterface.getInetAddresses();
        InetAddress currentAddress;
        while (inetAddress.hasMoreElements()) {
            currentAddress = inetAddress.nextElement();
            if (currentAddress instanceof Inet4Address && !currentAddress.isLoopbackAddress()) {
                return (Inet4Address) currentAddress;
            }
        }
        return null;
    }
}