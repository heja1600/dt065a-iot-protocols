package server.receiver;

import java.util.Arrays;

import listener.MessageReceiverCallback;
import listener.ServerListener;
import parser.MessageParser;
import util.ByteUtil;

public abstract class MessageReceiver<ParentMessageReceiver, Message> extends Thread {

    private boolean runServer;
    protected int packetSize;
    protected Integer port;

    protected MessageParser<Message> parser;
    protected byte[] buffer;

    private ServerListener<Message> serverListener;

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
    public ParentMessageReceiver setListener(ServerListener<Message> serverListener) {
        this.serverListener = serverListener;
        return (ParentMessageReceiver) this;
    }

    @SuppressWarnings("unchecked")
    public ParentMessageReceiver setPort(int port) {
        this.port = port;
        return (ParentMessageReceiver) this;
    }

    protected void triggerOnMessageRecieved(byte [] buffer, MessageReceiverCallback<Message> callback) {
        if(this.serverListener != null) {
            ByteUtil.printBytesAsString(Arrays.copyOf(buffer, 10));
            Message message = this.parser.decode(buffer);
            serverListener.onMessageReceived(message, callback);
        }
    }
    
}