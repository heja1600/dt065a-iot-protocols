package server.receiver;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import listener.ClientConnectionListener;
import listener.UniformCallback;
import parser.MessageParser;

public class TCPMessageReceiver<Message> extends MessageReceiver<TCPMessageReceiver<Message>, Message> {

    ServerSocket serverSocket;


    public TCPMessageReceiver(MessageParser<Message> parser) {
        super(parser);
    }

    
    @Override
    protected void serverEventLoop() {
        try {
            Socket socket = serverSocket.accept();
            OutputStream outputStream = socket.getOutputStream();
            InputStream inputStream = socket.getInputStream();

            ClientConnectionListener<Message> connectionReceiver = new ClientConnectionListener<>() {
                @Override
                public void receivePacket(UniformCallback<Message> callback) {
                    new Thread(() -> {
                        while(!socket.isClosed()) {
                            try {
                                inputStream.read(buffer);
                                callback.call(parser.decode(buffer));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }

                @Override
                public void close() {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void send(Message message) {
                    byte [] buffer = parser.encode(message);
                    try {
                        outputStream.write(buffer);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };
            if(clientConnectListener != null) {
                clientConnectListener.onClientConnect(connectionReceiver);
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void onInit() {
        try {
            serverSocket = new ServerSocket(port);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
