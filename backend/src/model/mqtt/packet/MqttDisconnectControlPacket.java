package model.mqtt.packet;

import model.mqtt.MqttControlPacketType;

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
