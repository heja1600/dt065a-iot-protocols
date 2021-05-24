package src.model.mqtt.packet;

import src.model.mqtt.MqttControlPacketType;

public class MqttDisconnectControlPacket extends AbstractMqttControlPacket {

    @Override
    public MqttControlPacketType getType() {
        return MqttControlPacketType.DISCONNECT;
    }

    @Override
    public int getFixedHeaderFlags() {
        return 0;
    }
}
