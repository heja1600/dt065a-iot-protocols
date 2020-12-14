package program;

import shared.model.mqtt.MqttMessage;
import shared.model.mqtt.packet.MqttConnectControlPacket;

public class MqttClientProgram {
    public static void main(String [] args) {
        MqttMessage<MqttConnectControlPacket> message = new MqttMessage<MqttConnectControlPacket>()
            .setPacket(
                new MqttConnectControlPacket()
            );
    }
}
