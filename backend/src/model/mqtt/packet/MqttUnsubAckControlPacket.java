package model.mqtt.packet;

import model.mqtt.MqttControlPacketType;
import model.mqtt.MqttPacketIdentifier;

public class MqttUnsubAckControlPacket extends AbstractMqttControlPacket implements MqttPacketIdentifier {

    private int packetIdentifier;
    
    @Override
    public MqttControlPacketType getType() {
        return MqttControlPacketType.UNSUBACK;
    }

    @Override
    public int getFixedHeaderFlags() {
        return 0;
    }

    @Override
    public int getPacketIdentifier() {
        return packetIdentifier;
    }

    public MqttUnsubAckControlPacket setPacketIdentifier(int packetIdentifier) {
        this.packetIdentifier = packetIdentifier;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("- Mqtt UnsubAck Control Packet" + "\n");
        builder.append("Packet identifier: " + getPacketIdentifier() + "\n");
        System.out.print("\n");
        return builder.toString();
    }
    
}
