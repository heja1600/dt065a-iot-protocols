package program;

import server.MQTTBrokerServer;
import shared.model.mqtt.MqttMessage;
import shared.model.mqtt.packet.MqttConnectControlPacket;

public class MqttClientProgram {
    
    MQTTBrokerServer server;
    public static void main(String [] args) {
        MqttMessage message = new MqttMessage()
            .setPacket(
                new MqttConnectControlPacket()
            );
    }
    
}
