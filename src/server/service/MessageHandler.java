package server.service;

import shared.model.*;

public class MessageHandler {
    public MessageHandler() {

    }
    public void handleMessage(CoapMessage message) {
        if(message.getCoapCode() == null) {
            System.out.println("Tomt message");
            return;
        }
        System.out.println("Inkommande message");
        switch(message.getCoapCode()) {
            case GET: {
                break;		
            }
            case POST: {
                break;		

            }
            case PUT: {
                break;		

            }
            case DELETE: {
                break;		

            }
        }
    }
}
