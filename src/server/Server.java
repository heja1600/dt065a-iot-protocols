package server;

import server.listeners.ServerListener;
import server.service.MessageHandler;
import server.service.ServerHandler;
import shared.model.coap.CoapMessage;

public class Server<T extends MessageHandler<T>> implements ServerListener {

    public enum ServerType {
        UDP, TCP;
    }

    ServerHandler serverHandler;
    MessageHandler<T> messageHandler;

    public Server(Class<T> serverType) {
        try {
            serverHandler = new ServerHandler();
            messageHandler = serverType
                .getDeclaredConstructor()
                .newInstance()
                .setListener(this)
                .setPacketLength(1024);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void startServer() {
        messageHandler.start();
    }

    @Override
    public void onMessageReceived(CoapMessage message) {
       serverHandler.handleMessage(message);
    }

    public void stopServer() {
        this.messageHandler.stopReceiving();
    }
}

