package server.service;

import java.util.Arrays;

import server.listener.MessageCallback;
import server.listener.ServerListener;
import shared.config.ServerConfig;
import shared.service.MessageParser;
import shared.util.ByteUtil;

public abstract class MessageReceiver<ParentMessageReceiver, Message> extends Thread {

    private boolean runServer;
    private int packetSize;

    protected MessageParser<Message> parser;
    protected byte[] buffer;

    private ServerListener<Message> serverListener;

    protected abstract void serverEventLoop();
    
    @SuppressWarnings("unchecked")
    MessageReceiver(MessageParser<Message> parser) {
        this.parser = parser;
        try {

            packetSize = 1024;
            buffer = new byte[packetSize];	
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    }

    @Override
    public void run() {
        runServer = true;
        while(runServer) {
            System.out.println("Listening on port " + ServerConfig.COAP_SERVER_PORT);
            serverEventLoop();
        }
        super.run();
    }
    public void stopReceiving() {
        this.runServer = false;
    }

    @SuppressWarnings("unchecked")
    public ParentMessageReceiver setPacketLength(int packetSize) {
        this.packetSize = packetSize;
        return (ParentMessageReceiver) this;
    }


    @SuppressWarnings("unchecked")
    public ParentMessageReceiver setListener(ServerListener<Message> serverListener) {
        this.serverListener = serverListener;
        return (ParentMessageReceiver) this;
    }


    protected void triggerOnMessageRecieved(byte [] buffer, MessageCallback<Message> callback) {
        if(this.serverListener != null) {
            ByteUtil.printBytesAsString(Arrays.copyOf(buffer, 10));
            Message message = this.parser.decode(buffer);
            serverListener.onMessageReceived(message, callback);
        }
    }
    
}