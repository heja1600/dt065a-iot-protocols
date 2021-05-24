package src.server.receiver;

import src.listener.ClientConnectListener;
import src.parser.MessageParser;

public abstract class MessageReceiver<ParentMessageReceiver, Message> extends Thread {

    private boolean runServer;
    protected int packetSize;
    protected Integer port;
    protected String hostname;

    protected MessageParser<Message> parser;
    protected byte[] buffer;

    protected ClientConnectListener<Message> clientConnectListener;

    protected abstract void serverEventLoop();

    protected abstract void onInit();

    protected abstract void onDestroy();

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
        onInit();
        runServer = true;
        while(runServer) {
            System.out.println("Listening on port " + port);
            serverEventLoop();
        }
        onDestroy();

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
    public ParentMessageReceiver setListener(ClientConnectListener<Message> clientConnectListener) {
        this.clientConnectListener = clientConnectListener;
        return (ParentMessageReceiver) this;
    }

    @SuppressWarnings("unchecked")
    public ParentMessageReceiver setPort(int port) {
        this.port = port;
        return (ParentMessageReceiver) this;
    }
    
    @SuppressWarnings("unchecked")
    public ParentMessageReceiver setHostname(String hostname) {
        this.hostname = hostname;
        return (ParentMessageReceiver) this;
    }
}