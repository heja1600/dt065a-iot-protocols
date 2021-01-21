package model.mqtt.packet;

import model.mqtt.MqttControlPacketType;

public abstract class AbstractMqttControlPacket {

    MqttControlPacketType type;

    public AbstractMqttControlPacket(MqttControlPacketType type) {
        this.setType(type);
    }
    public MqttControlPacketType getType() {
        return type;
    }

    private void setType(MqttControlPacketType type) {
        this.type = type;
    }
}
