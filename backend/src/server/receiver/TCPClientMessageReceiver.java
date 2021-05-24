package src.server.receiver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;

import src.listener.ClientConnectionListener;
import src.listener.UniformCallback;
import src.parser.MessageParser;

public class TCPClientMessageReceiver<Message> extends MessageReceiver<TCPClientMessageReceiver<Message>, Message>  {
    Socket socket;
    public TCPClientMessageReceiver(MessageParser<Message> parser) {
        super(parser);
        //TODO Auto-generated constructor stub
    }

    @Override
    protected void serverEventLoop() {
        try {
            OutputStream outputStream = socket.getOutputStream();
            InputStream inputStream = socket.getInputStream();
            ClientConnectionListener<Message> connectionReceiver = new ClientConnectionListener<>() {
                @Override
                public void receivePacket(UniformCallback<Message> callback) {
                    new Thread(() -> {
                        while(!socket.isClosed() && socket.isConnected()) {
                            try {
                                byte [] buffer = new byte[2048];
                                inputStream.read(buffer);
                                callback.call(parser.decode(buffer));
                            }catch(SocketException e) {
                                try {
                                    socket.close();
                                } catch (IOException e1) {
                                    // TODO Auto-generated catch block
                                    e1.printStackTrace();
                                }
                                
                            }catch (Exception e) {
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

            while(socket.isConnected()) {
                /** Dono */
            }




        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    protected void onInit() {
        try {
            socket = new Socket(hostname, port);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        try {
            socket.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
}
