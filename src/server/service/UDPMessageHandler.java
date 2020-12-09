package server.service;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Arrays;

import shared.config.ServerConfig;
import shared.service.MessageParser;
import shared.util.ByteUtil;

public class UDPMessageHandler<Message, Parser extends MessageParser<Message>> extends MessageReceiver<UDPMessageHandler<Message,Parser>, Message, Parser> {

    DatagramSocket datagramSocket;
    DatagramPacket recievePacket;

    public UDPMessageHandler() {

        try {
            datagramSocket = new DatagramSocket(ServerConfig.SERVER_PORT);
            recievePacket = new DatagramPacket(buffer, buffer.length);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void serverEventLoop() {
        try {
            datagramSocket.receive(recievePacket);
            byte[] packetData = recievePacket.getData();
            this.triggerOnMessageRecieved(packetData);
            ByteUtil.printBytesAsString(Arrays.copyOf(buffer, 20));

            // InetAddress clientAddress = recievePacket.getAddress();
            // int clientPort = recievePacket.getPort();
            // buffer = parser.createCoapMessage(new CoapMessage().setType(CoapType.ACK));

            // DatagramPacket response = new DatagramPacket(buffer, buffer.length, clientAddress, clientPort);
            // datagramSocket.send(response);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
