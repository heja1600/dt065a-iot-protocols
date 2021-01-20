package server;

import server.listener.ExtendedServerListener;
import server.listener.MessageCallback;
import server.listener.ServerListener;
import server.service.CoapMessageHandler;
import server.service.MessageReceiver;
import shared.config.ServerConfig;
import shared.model.coap.CoapMessage;

public class CoapServer<T extends MessageReceiver<T, CoapMessage>> implements ServerListener<CoapMessage> {

    public enum ServerType {
        UDP, TCP;
    }
    ExtendedServerListener<CoapMessage> serverListener;
    CoapMessageHandler serverHandler;
    T messageReceiver;

    boolean running = false;
    public CoapServer(T messageReceiver) {
        try {
            serverHandler = new CoapMessageHandler();
            this.messageReceiver = messageReceiver;
            messageReceiver
                .setListener(this)
                .setPacketLength(1024)
                .setPort(ServerConfig.COAP_SERVER_PORT);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public CoapServer<T> setListener(ExtendedServerListener<CoapMessage> serverListener) {
        this.serverListener = serverListener;
        return this;
    }
    public void startServer() {
        running = true;
        messageReceiver.start();
    }

    public void stopServer() {
        running = false;
        this.messageReceiver.stopReceiving();
    }

    public boolean isRunning() {
        return running;
    }

    @Override
    public void onMessageReceived(CoapMessage message, MessageCallback<CoapMessage> callback) {
        if(this.serverListener != null){
            this.serverListener.onMessageReceived(message);
        }

        CoapMessage responseMessage = serverHandler.handleMessage(message);

        if(this.serverListener != null){
            this.serverListener.onMessageSent(responseMessage);
        }

        callback.respond(serverHandler.handleMessage(responseMessage));
      
    }
}

