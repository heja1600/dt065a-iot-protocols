package server.handler;

import model.mqtt.MqttMessage;
import model.mqtt.packet.MqttConnAckControlPacket;
import model.mqtt.packet.MqttConnAckControlPacket.MqttConnectReturnCode;
import model.mqtt.packet.MqttConnectControlPacket;

public class MqttMessageHandler {
    public MqttMessageHandler() {

    }
    public MqttMessage handleMessage(MqttMessage request) throws Exception {

        System.out.println("@ <- Received packet: \n" + request);
        MqttMessage response = new MqttMessage();

        switch(request.getMqttControlPacketType()) {
            case CONNECT: {
                MqttConnectControlPacket requestPacket = ((MqttConnectControlPacket)request.getPacket());

                MqttConnAckControlPacket responsePacket = new MqttConnAckControlPacket()
                    .setMqttConnectReturnCode(MqttConnectReturnCode.CONNECTION_ACCEPTED)
                    .setSessionPresentFlag(!requestPacket.getConnectFlag().isCleanSessionFlag());

                response.setPacket(responsePacket);
                break;
            }
            case CONNACK:
                break;
            case DISCONNECT:
                break;
            case PINGREQ:
                break;
            case PINGRESP:
                break;
            case PUBLISH:
                break;
            case SUBCRIBE:
                break;
            case UNSUBSCRIBE:
                break;
            default:
                throw new Exception("MQTT Message Handler does not support: " + request.getMqttControlPacketType());
        }
  
        System.out.println("@ -> Sending back packet: \n" + response);

        return response;
    }
}
