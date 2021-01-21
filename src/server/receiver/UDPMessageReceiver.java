package server.receiver;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import listener.MessageReceiverCallback;
import parser.MessageParser;

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
            MessageReceiverCallback<Message> callback = new MessageReceiverCallback<Message>() {
                
				@Override
				public void respond(Message message) {
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
					
				}

				@Override
				public void close() {
			
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
