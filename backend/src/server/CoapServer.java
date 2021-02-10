package server;

import config.ServerConfig;
import listener.ClientConnectListener;
import listener.ClientConnectionListener;
import listener.ServerListener;
import model.coap.CoapMessage;
import server.handler.CoapMessageHandler;
import server.receiver.MessageReceiver;

public class CoapServer<T extends MessageReceiver<T, CoapMessage>> implements ClientConnectListener<CoapMessage> {

    public enum ServerType {
        UDP, TCP;
    }

    ServerListener<CoapMessage> serverListener;
    CoapMessageHandler serverHandler;
    T messageReceiver;

    boolean running = false;

    public CoapServer(T messageReceiver) {
        try {
            serverHandler = new CoapMessageHandler();
            this.messageReceiver = messageReceiver;
            messageReceiver.setListener(this).setPacketLength(1024).setPort(ServerConfig.COAP_SERVER_PORT);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
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

            CoapMessage responseMessage = serverHandler.handleMessage(message);

            if (this.serverListener != null) {
                this.serverListener.onMessageSent(responseMessage);
            }
    
            connection.send(responseMessage);
            connection.close();
        });
    }
}

