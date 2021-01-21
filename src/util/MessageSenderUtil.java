package util;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

import listener.MessageCallback;

public class MessageSenderUtil {
    public static void udpSendMessage(byte[] buffer, int port, String hostname) throws Exception {
        DatagramSocket datagramSocket = new DatagramSocket();
        InetAddress address = InetAddress.getByName(hostname);
        DatagramPacket sendPacket = new DatagramPacket(buffer, buffer.length, address, port);
        datagramSocket.send(sendPacket);
        datagramSocket.close();
    }

    public static void udpSendAndRecieve(
        byte[] packet, 
        int port, 
        String hostname, 
        int recieveSize, 
        MessageCallback<byte[]> callback
    ) throws IOException {
        DatagramSocket datagramSocket = new DatagramSocket();
        InetAddress address = InetAddress.getByName(hostname);
        DatagramPacket sendPacket = new DatagramPacket(packet, packet.length, address, port);
        datagramSocket.send(sendPacket);

        new Thread(new Runnable(){
            public void run(){
                try {
                    DatagramPacket receivePacket = new DatagramPacket(new byte[recieveSize], recieveSize);
                    datagramSocket.receive(receivePacket);
                    datagramSocket.close();
                    callback.send(receivePacket.getData());
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static void tcpSendMessage(byte[] packet,int port, String hostname) {
        try (var socket = new Socket(hostname, port)) {
            ByteUtil.printBytesAsString(packet);
            socket.getOutputStream().write(packet);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void tcpSendAndReceive(
        byte[] packet,
        int port, 
        String hostname,       
        MessageCallback<byte[]> callback
    ) {
        
        try {
            var socket = new Socket(hostname, port);
            ByteUtil.printBytesAsString(packet);
            socket.getOutputStream().write(packet);
            new Thread(new Runnable(){
                public void run(){
                    try {
                        callback.send(socket.getInputStream().readAllBytes());
                        socket.close();
                        System.out.println("sendign back");
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch(Exception e) {
            e.printStackTrace();
        } finally {}
    }
}
