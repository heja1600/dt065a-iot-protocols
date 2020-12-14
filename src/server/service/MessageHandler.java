package server.service;

import shared.model.coap.CoapCode;
import shared.model.coap.CoapMessage;
import shared.service.CoapMessageParser;

public class MessageHandler {
    public MessageHandler() {

    }
    public CoapMessage handleMessage(CoapMessage message) {


        System.out.println("Received packet: ");
        System.out.println(message);
        System.out.println(message);

        CoapMessage responseMessage = new CoapMessage().setCode(CoapCode.BAD_REQUEST);
        System.out.println("Sending back packet");
        System.out.println(responseMessage);
        return responseMessage;
        // if(message.getCode() == null) {
        //     System.out.println("Tomt message");
        //     new CoapMessage().setCode(CoapCode.BAD_REQUEST);
        // }

        // switch(message.getCode()) {
        //     case GET: {
        //         break;		
        //     }
        //     case POST: {
        //         break;		

        //     }
        //     case PUT: {
        //         break;		

        //     }
        //     case DELETE: {
        //         break;		

        //     }
        //     default:
        //         break;
        // }
    }
}
