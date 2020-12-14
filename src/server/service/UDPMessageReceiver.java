package server.service;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import server.listener.MessageCallback;
import shared.service.MessageParser;

public class UDPMessageReceiver <Message> extends MessageReceiver<UDPMessageReceiver<Message>, Message>
{

    DatagramSocket datagramSocket;
    DatagramPacket recievePacket;

    public UDPMessageReceiver(MessageParser<Message> parser) {
        super(parser);
    
    }

    @Override
    protected void serverEventLoop() {
        try {
            datagramSocket.receive(recievePacket);
            byte[] packetData = recievePacket.getData();
            System.out.println("Message Received");
            MessageCallback<Message> callback = message -> {
                try {
                    InetAddress clientAddress = recievePacket.getAddress();
                    int clientPort = recievePacket.getPort();
                    buffer = parser.encode(message);

                    DatagramPacket response = new DatagramPacket(buffer, buffer.length, clientAddress, clientPort);
                    datagramSocket.send(response);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            };

            this.triggerOnMessageRecieved(packetData, callback);
    
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    protected void onInit() {
        try {
            datagramSocket = new DatagramSocket(port);
            recievePacket = new DatagramPacket(buffer, buffer.length);

        } catch(Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onDestroy() {
        datagramSocket.close();
    }
}
