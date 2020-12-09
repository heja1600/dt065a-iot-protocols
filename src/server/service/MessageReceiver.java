package server.service;

import java.util.Arrays;

import server.listener.ServerListener;
import shared.config.ServerConfig;
import shared.service.MessageParser;
import shared.util.ByteUtil;

public abstract class MessageReceiver<T extends MessageReceiver<T, Message, Parser>, Message, Parser extends MessageParser<Message>> extends Thread {

    private boolean runServer;
    private int packetSize;
  
    protected Parser parser;
    protected byte[] buffer;
    protected abstract void serverEventLoop();
    private ServerListener<Message> serverListener;

    MessageReceiver() {
        // this.parser = clazz.getDeclaredConstructor().newInstance();
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
    public T setListener(ServerListener<Message> serverListener) {
        this.serverListener = serverListener;
        return (T) this;
    }


    protected void triggerOnMessageRecieved(byte [] buffer) {
        if(this.serverListener != null) {
            ByteUtil.printBytesAsString(Arrays.copyOf(buffer, 10));
            Message message = this.parser.decode(buffer);
            this.serverListener.onMessageReceived(message);
        }
    }
}