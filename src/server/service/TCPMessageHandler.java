package server.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import shared.config.ServerConfig;

public class TCPMessageHandler extends MessageHandler<TCPMessageHandler> {
    ServerSocket serverSocket;

    public TCPMessageHandler() {
        try {
            serverSocket = new ServerSocket(ServerConfig.SERVER_PORT);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
	}

	
    @Override
    protected void serverEventLoop() {
        try {
            Socket socket = serverSocket.accept();
            OutputStream outputStream = socket.getOutputStream();
            InputStream inputStream = socket.getInputStream();
            inputStream.read(buffer);
            this.triggerOnMessageRecieved(buffer);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
