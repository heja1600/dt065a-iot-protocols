package shared.model.mqtt.packet;

import shared.model.mqtt.MqttControlPacketType;

public abstract class AbstractMqttControlPacket {

    MqttControlPacketType type;

    AbstractMqttControlPacket(MqttControlPacketType type) {

    }
    public MqttControlPacketType getType() {
        return type;
    }

    public void setType(MqttControlPacketType type) {
        this.type = type;
    }
}
