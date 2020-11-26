package server.service;

import shared.model.coap.CoapMessage;

public class MessageHandler {
    public MessageHandler() {

    }
    public void handleMessage(CoapMessage message) {
        if(message.getCode() == null) {
            System.out.println("Tomt message");
            return;
        }

        switch(message.getCode()) {
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
