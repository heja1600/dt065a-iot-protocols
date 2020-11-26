package server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import server.listeners.OnMessageRetrieved;
import server.service.MessageHandler;
import shared.config.ServerConfig;
import shared.model.coap.CoapMessage;
import shared.service.CoapMesssageParser;

public class Server extends Thread {

    ServerSocket serverSocket;
    boolean runServer;
    MessageHandler messageHandler;
    int packetSize = 1024;
    OnMessageRetrieved onMessageRetrieved;
    CoapMesssageParser coapMesssageParser;
    Server(OnMessageRetrieved onMessageRetrieved) {
        this.onMessageRetrieved = onMessageRetrieved;
         try {
            serverSocket = new ServerSocket(ServerConfig.SERVER_PORT);
            messageHandler = new MessageHandler();
            coapMesssageParser = new CoapMesssageParser();
        } catch(Exception e) {
            e.printStackTrace();
        }   
    }
    @Override
    public void run() {
        runServer = true;

        while(runServer) {

            try {
                Socket socket = serverSocket.accept();
                OutputStream outpuStream = socket.getOutputStream();
                InputStream inputStream = socket.getInputStream();
                byte [] buffer = new byte[this.packetSize];
                inputStream.read(buffer);
        
                CoapMessage message = coapMesssageParser.parseCoapMessage(buffer);
                onMessageRetrieved.onMessageRetrieved(message);

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}


        }
        // TODO Auto-generated method stub
        super.run();
    }
}