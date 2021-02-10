package model.mqtt.packet;

import model.mqtt.MqttControlPacketType;
import model.mqtt.MqttPacketIdentifier;

public class MqttPubAckControlPacket extends AbstractMqttControlPacket implements MqttPacketIdentifier {

    private int packetIdentifier;
    @Override
    public MqttControlPacketType getType() {
        return MqttControlPacketType.PUBACK;
    }

    @Override
    public int getFixedHeaderFlags() {
        return 0;
    }

    public int getPacketIdentifier() {
        return packetIdentifier;
    }

    public MqttPubAckControlPacket setPacketIdentifier(int packetIdentifier) {
        this.packetIdentifier = packetIdentifier;
        return this;
    }


    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("- Mqtt PubAck Control Packet" + "\n");
        builder.append("Packet identifier: " + getPacketIdentifier() + "\n" );
        return builder.toString();
    }

}
