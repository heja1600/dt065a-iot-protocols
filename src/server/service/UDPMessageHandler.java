package server.service;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;

import shared.config.ServerConfig;
import shared.model.coap.CoapMessage;
import shared.model.coap.CoapType;
import shared.util.ByteUtil;

public class UDPMessageHandler extends MessageHandler<UDPMessageHandler> {

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

            InetAddress clientAddress = recievePacket.getAddress();
            int clientPort = recievePacket.getPort();
            buffer = coapMesssageParser.createCoapMessage(new CoapMessage().setType(CoapType.ACK));

            DatagramPacket response = new DatagramPacket(buffer, buffer.length, clientAddress, clientPort);
            datagramSocket.send(response);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
