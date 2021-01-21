package model.mqtt.packet;

import model.mqtt.MqttControlPacketType;

public class MqttPingRequestPacket extends AbstractMqttControlPacket {

    @Override
    public MqttControlPacketType getType() {
        return MqttControlPacketType.PINGREQ;
    }
    @Override
    public int getFixedHeaderFlags() {
        return 0;
    }
}
