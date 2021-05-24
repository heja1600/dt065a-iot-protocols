package src.server.handler;

import src.model.coap.CoapCode;
import src.model.coap.CoapMessage;

public class CoapMessageHandler implements MessageHandler<CoapMessage>{
    public CoapMessageHandler() {

        
    }
    
    @Override
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
