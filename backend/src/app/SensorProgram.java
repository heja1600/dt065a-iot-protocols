package src.app;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import src.listener.ServerListener;
import src.model.coap.CoapCode;
import src.model.coap.CoapMessage;
import src.parser.CoapMessageParser;
import src.server.CoapServer;
import src.server.handler.MessageHandler;
import src.server.receiver.UDPMessageReceiver;
import src.util.CoapUtil;

public class SensorProgram implements ServerListener<CoapMessage>, MessageHandler<CoapMessage> {
    CoapServer<UDPMessageReceiver<CoapMessage>> coapServer;
    public static void main(String [] args) {
        new SensorProgram();
    }

    public SensorProgram() {
        coapServer = new CoapServer<>(new UDPMessageReceiver<CoapMessage>(new CoapMessageParser()))
            .setListener(this)
            .setMessageHandler(this);

        coapServer.startServer();
    }

    @Override
    public void onMessageReceived(CoapMessage message) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onMessageSent(CoapMessage message) {
        // TODO Auto-generated method stub
    }

    @Override
    public CoapMessage handleMessage(CoapMessage message) {
        if(message.getCode() == null) {
            return new CoapMessage().setCode(CoapCode.BAD_REQUEST);
        }
        String url = CoapUtil.getUrl(message);
        System.out.println("uri:" + url+ ", method:" + message.getCode());
        switch(message.getCode()) {
            case GET: {
                if(CoapUtil.isUrl(message, "pi", "time")) {
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");  
                    LocalDateTime now = LocalDateTime.now();  
                    return new CoapMessage()
                        .setCode(CoapCode.VALID)
                        .setPayload(dtf.format(now));
                }
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
            default:
                break;
        }
        return new CoapMessage().setCode(CoapCode.BAD_REQUEST);
    }
}
