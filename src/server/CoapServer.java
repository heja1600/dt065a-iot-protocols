package server;

import server.listener.MessageCallback;
import server.listener.ServerListener;
import server.service.MessageHandler;
import server.service.MessageReceiver;
import shared.model.coap.CoapMessage;

public class CoapServer<T extends MessageReceiver<T, CoapMessage>> implements ServerListener<CoapMessage> {

    public enum ServerType {
        UDP, TCP;
    }

    MessageHandler serverHandler;
    T messageReceiver;

    public CoapServer(T messageReceiver) {
        try {
            serverHandler = new MessageHandler();
            this.messageReceiver = messageReceiver;
            messageReceiver.setListener(this);
            messageReceiver.setPacketLength(1024);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void startServer() {
        messageReceiver.start();
    }

    public void stopServer() {
        this.messageReceiver.stopReceiving();
    }

    @Override
    public void onMessageReceived(CoapMessage message, MessageCallback<CoapMessage> callback) {
        callback.respond(serverHandler.handleMessage(message));
    }
}

