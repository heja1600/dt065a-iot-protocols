package server.service;

import java.util.Arrays;

import server.listeners.ServerListener;
import shared.config.ServerConfig;
import shared.model.coap.CoapMessage;
import shared.service.CoapMesssageParser;
import shared.util.ByteUtil;

public abstract class MessageHandler<T extends MessageHandler<T>> extends Thread {

    private boolean runServer;
    private int packetSize;
  
    protected CoapMesssageParser coapMesssageParser;
    protected byte[] buffer;
    protected abstract void serverEventLoop();
    private ServerListener serverListener;

    MessageHandler() {

        packetSize = 1024;
        buffer = new byte[packetSize];	
    
        this.coapMesssageParser = new CoapMesssageParser();
    }

    @Override
    public void run() {
        runServer = true;
        while(runServer) {
            System.out.println("Listening on port " + ServerConfig.SERVER_PORT);
            serverEventLoop();
        }
        super.run();
    }
    public void stopReceiving() {
        this.runServer = false;
    }

    @SuppressWarnings("unchecked")
    public T setPacketLength(int packetSize) {
        this.packetSize = packetSize;
        return (T) this;
    }


    @SuppressWarnings("unchecked")
    public T setListener(ServerListener serverListener) {
        this.serverListener = serverListener;
        return (T) this;
    }


    protected void triggerOnMessageRecieved(byte [] buffer) {
        if(this.serverListener != null) {
            ByteUtil.printBytesAsString(Arrays.copyOf(buffer, 10));
            CoapMessage message = this.coapMesssageParser.parseCoapMessage(buffer);
            this.serverListener.onMessageReceived(message);
        }
    }
}