package model.mqtt.packet;

import model.mqtt.MqttControlPacketType;

public abstract class AbstractMqttControlPacket {

    MqttControlPacketType type;
    
    public abstract MqttControlPacketType getType();

    public abstract int getFixedHeaderFlags();

    @Override
    public String toString() {
        return "";
    }
}
