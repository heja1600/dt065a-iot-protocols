package server.service;

import shared.model.mqtt.MqttMessage;

public class MqttMessageHandler {
    public MqttMessageHandler() {

    }
    public MqttMessage handleMessage(MqttMessage message) {


        System.out.println("Received packet: ");
        System.out.println(message);
        System.out.println(message);

  
        System.out.println("Sending back packet");
   
        return null;
        // if(message.getCode() == null) {
        //     System.out.println("Tomt message");
        //     new MqttMessage().setCode(CoapCode.BAD_REQUEST);
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
