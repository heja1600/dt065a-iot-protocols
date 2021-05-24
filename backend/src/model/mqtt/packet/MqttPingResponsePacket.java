package src.model.mqtt.packet;

import src.model.mqtt.MqttControlPacketType;

public class MqttPingResponsePacket extends AbstractMqttControlPacket {

    @Override
    public MqttControlPacketType getType() {
        return MqttControlPacketType.PINGRESP;
    }
    
    @Override
    public int getFixedHeaderFlags() {
        return 0;
    }
}
