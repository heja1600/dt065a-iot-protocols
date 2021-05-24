package src.util;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

import src.listener.UniformCallback;

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
        UniformCallback<byte[]> callback
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
                    callback.call(receivePacket.getData());
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static void tcpSendMessage(byte[] packet,int port, String hostname) {
        try (var socket = new Socket(hostname, port)) {
            socket.getOutputStream().write(packet);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void tcpSendAndReceive(
        byte[] packet,
        int port, 
        String hostname,       
        UniformCallback<byte[]> callback
    ) {
        
        try {
            var socket = new Socket(hostname, port);
            socket.getOutputStream().write(packet);
            new Thread(new Runnable(){
                public void run(){
                    try {
                        callback.call(socket.getInputStream().readAllBytes());
                        socket.close();
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


    public static void tcpSend(
        byte[] packet,
        int port, 
        String hostname
    ) {
        try {
            var socket = new Socket(hostname, port);
            socket.getOutputStream().write(packet);
            socket.close();
        } catch(Exception e) {
            e.printStackTrace();
        } finally {}
    }
    

    public static void tcpReceive(String hostname, Integer port, int recieveSize,  UniformCallback<byte[]> callback) throws Exception {
 
        try (var socket = new Socket(hostname, port)) {
            try (var is = socket.getInputStream()){
                byte [] buffer = new byte[recieveSize];
                is.read(buffer);
                callback.call(buffer);
            }
        }
    }
}
