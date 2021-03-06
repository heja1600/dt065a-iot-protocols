package src.server.receiver;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;

import src.listener.ClientConnectionListener;
import src.listener.UniformCallback;
import src.parser.MessageParser;

public class UDPMessageReceiver<Message> extends MessageReceiver<UDPMessageReceiver<Message>, Message> {

    DatagramSocket datagramSocket;
    DatagramPacket recievePacket;

    HashMap<InetAddress, UniformCallback<Message>> receivers;

    public UDPMessageReceiver(MessageParser<Message> parser) {
        super(parser);
        receivers = new HashMap<>();

    }

    @Override
    protected void serverEventLoop() {
        try {
            datagramSocket.receive(recievePacket);
            byte[] packetData = recievePacket.getData();
            InetAddress clientAddress = recievePacket.getAddress();
            if (!receivers.containsKey(clientAddress)) {
                ClientConnectionListener<Message> reciever = new ClientConnectionListener<Message>() {

                    @Override
                    public void receivePacket(UniformCallback<Message> callback) {
                        callback.call(parser.decode(packetData));
                        receivers.put(clientAddress, callback);
                    }

                    @Override
                    public void close() {
                        receivers.remove(clientAddress);
                    }

                    @Override
                    public void send(Message message) {
                        try {
                            int clientPort = recievePacket.getPort();
                            buffer = parser.encode(message);

                            DatagramPacket response = new DatagramPacket(buffer, buffer.length, clientAddress, clientPort);
                            datagramSocket.send(response);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                };
                if(clientConnectListener != null) {
                    clientConnectListener.onClientConnect(reciever);
                }

                
            }
            if(receivers.containsKey(clientAddress)) {
                receivers.get(clientAddress).call(parser.decode(buffer));
            }
     
    
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    @Override
    protected void onInit() {
        try {
            datagramSocket = new DatagramSocket(port, InetAddress.getByName("0.0.0.0"));
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
