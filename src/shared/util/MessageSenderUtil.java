package shared.util;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

public class MessageSenderUtil {
    public static void udpSendMessage(byte[] buffer, int port, String hostname) throws Exception {
        DatagramSocket datagramSocket = new DatagramSocket();
        InetAddress address = InetAddress.getByName(hostname);
        DatagramPacket sendPacket = new DatagramPacket(buffer, buffer.length, address, port);
        datagramSocket.send(sendPacket);
        datagramSocket.close();
    }

    public static byte[] udpSendAndRecieve(byte[] packet, int port, String hostname, int recieveSize) throws IOException {
        DatagramSocket datagramSocket = new DatagramSocket();
        InetAddress address = InetAddress.getByName(hostname);
        DatagramPacket sendPacket = new DatagramPacket(packet, packet.length, address, port);
        datagramSocket.send(sendPacket);
        DatagramPacket receivePacket = new DatagramPacket(new byte[recieveSize], recieveSize);
        datagramSocket.receive(receivePacket);
        datagramSocket.close();
        return receivePacket.getData();
    }

    public static void tcpSendMessage(byte[] packet,int port, String hostname) {
        try (var socket = new Socket(hostname, port)) {
            ByteUtil.printBytesAsString(packet);
            socket.getOutputStream().write(packet);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static byte [] tcpSendAndReceive(byte[] packet,int port, String hostname) {
        try (var socket = new Socket(hostname, port)) {
            ByteUtil.printBytesAsString(packet);
            socket.getOutputStream().write(packet);

            return socket.getInputStream().readAllBytes();
            
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
