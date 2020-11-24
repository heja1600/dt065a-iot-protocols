package shared.service;

import java.io.ByteArrayOutputStream;

import shared.model.CoapCode;
import shared.model.CoapMessage;

import shared.util.ByteUtil;

public class CoapMesssageParser {
    public byte [] createCoapMessage(CoapMessage coapMessage) {
        byte [] buffer;
        try(ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {

            /** Setting version and message type */
            byteArrayOutputStream.write(ByteUtil.bitToDec("01010000"));  

            /** Set response code or method */
            if(coapMessage.getCoapCode() != null) {
                int coapMethod = coapMessage.getCoapCode().get();
                byteArrayOutputStream.write(coapMethod);
            } 

            /** Set message id */
            byteArrayOutputStream.write(ByteUtil.bitToDec("10101010"));
            byteArrayOutputStream.write(ByteUtil.bitToDec("01010101"));

            buffer = byteArrayOutputStream.toByteArray();
        }catch(Exception e) 
        {   
            e.printStackTrace();
            return null;
        }

        System.out.println("Creating message of byte: " + new String(buffer));
 
        /** set Code (Method)*/
        return buffer;
    }   

    public CoapMessage parseCoapMessage(byte [] buffer) {
        
        CoapMessage coapMessage = new CoapMessage().setCoapCode(CoapCode.get(buffer[1]));
        
        System.out.println("Coap Code is: " + coapMessage.getCoapCode());

        /** Get coap code */
        
    
        return new CoapMessage();
    }
}
