package server;

import server.listener.ServerListener;
import server.service.MessageHandler;
import server.service.MessageReceiver;
import shared.model.coap.CoapMessage;
import shared.service.CoapMessageParser;
public class CoapServer<T extends MessageReceiver<T, CoapMessage, CoapMessageParser>> implements ServerListener<CoapMessage> {

    public enum ServerType {
        UDP, TCP;
    }

    MessageHandler serverHandler;
    MessageReceiver<T, CoapMessage, CoapMessageParser> messageHandler;

    public CoapServer(Class<T> serverType) {
        try {
            serverHandler = new MessageHandler();
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

