package server.receiver;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import listener.MessageReceiverCallback;
import parser.MessageParser;

public class TCPMessageReceiver <Message> extends MessageReceiver<TCPMessageReceiver<Message>, Message> 
{   
    
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
            inputStream.read(buffer);
            MessageReceiverCallback<Message> callback = new MessageReceiverCallback<Message>() {
                
				@Override
				public void respond(Message message) {
                    try {
                        byte [] buffer = parser.encode(message);
                        outputStream.write(buffer);
                        socket.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
					
				}

				@Override
				public void close() {
					try {
                        socket.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
				}  
            };
  
            this.triggerOnMessageRecieved(buffer, callback);
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
