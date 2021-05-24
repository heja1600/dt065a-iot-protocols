package src.model.mqtt.packet;

import src.model.mqtt.MqttControlPacketType;

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
