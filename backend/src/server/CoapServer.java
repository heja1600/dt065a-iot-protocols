package src.server;

import src.config.ServerConfig;
import src.listener.ClientConnectListener;
import src.listener.ClientConnectionListener;
import src.listener.ServerListener;
import src.model.coap.CoapMessage;
import src.server.handler.CoapMessageHandler;
import src.server.handler.MessageHandler;
import src.server.receiver.MessageReceiver;

public class CoapServer<T extends MessageReceiver<T, CoapMessage>> implements ClientConnectListener<CoapMessage> {

    public enum ServerType {
        UDP, TCP;
    }

    ServerListener<CoapMessage> serverListener;
    MessageHandler<CoapMessage>  messageHandler;
    T messageReceiver;

    boolean running = false;

    public CoapServer(T messageReceiver) {
        try {
            messageHandler = new CoapMessageHandler();
            this.messageReceiver = messageReceiver;
            messageReceiver.setListener(this).setPacketLength(1024).setPort(ServerConfig.COAP_SERVER_PORT);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    public CoapServer<T> setMessageHandler(MessageHandler<CoapMessage> messageHandler) {
        this.messageHandler = messageHandler;
        return this;
    }

    public CoapServer<T> setListener(ServerListener<CoapMessage> serverListener) {
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
    public void onClientConnect(ClientConnectionListener<CoapMessage> connection) {
        connection.receivePacket(message -> {
            if (this.serverListener != null) {
                this.serverListener.onMessageReceived(message);
            }


            CoapMessage responseMessage = messageHandler.handleMessage(message);

            if (this.serverListener != null) {
                this.serverListener.onMessageSent(responseMessage);
            }
    
            connection.send(responseMessage);
            connection.close();
        });
    }
}

