package server.service;

import shared.model.coap.CoapMessage;
import shared.service.CoapMesssageParser;

public class ServerHandler {
    public ServerHandler() {

    }
    public void handleMessage(CoapMessage message) {
        CoapMesssageParser.printCoapMessage(message);
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
