package server;


import server.listeners.OnMessageRetrieved;
import server.service.MessageHandler;
import shared.model.coap.CoapMessage;

public class Program implements OnMessageRetrieved {
    Server server;
    MessageHandler messageHandler;
    public static void main(String [] args) {
        Program program = new Program();
        program.startProgram();
    }
    Program() {
        messageHandler = new MessageHandler();
        server = new Server(this);
    }
    public void startProgram() {
        server.start();
    }
	@Override
	public void onMessageRetrieved(CoapMessage message) {
		messageHandler.handleMessage(message);
	}   
}