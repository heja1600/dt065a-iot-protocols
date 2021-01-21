package app;

import model.mqtt.MqttMessage;
import model.mqtt.packet.MqttConnectControlPacket;
import server.MQTTBrokerServer;

public class MqttClientApplication {
    
    MQTTBrokerServer server;
    public static void main(String [] args) {
        MqttMessage message = new MqttMessage()
            .setPacket(
                new MqttConnectControlPacket()
            );
    }
    
}
