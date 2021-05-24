package src.model.mqtt.packet;

import src.model.mqtt.MqttControlPacketType;
import src.model.mqtt.MqttPacketIdentifier;
import src.model.mqtt.MqttQoS;

public class MqttPublishControlPacket extends AbstractMqttControlPacket implements MqttPacketIdentifier {

    private int fixedHeaderFlags = 0;
    private int packetIdentifier;
    private String topic;
    private String payload;

    public MqttPublishControlPacket() {
        setDUPFlag(false);
        setRetainFlag(false);
        setMqttQoS(MqttQoS.AT_MOST_ONCE);
    }

    public boolean isDUPFlag() {
        return (fixedHeaderFlags & 0b1000) >> 3 == 1;
    }


    public MqttPublishControlPacket setDUPFlag(boolean DUPFlag) {
        fixedHeaderFlags = (DUPFlag ? 0b1000 : 0) & fixedHeaderFlags;
        return this;
    }
    public MqttQoS getMqttQoS() {
        return MqttQoS.get((fixedHeaderFlags & 0b0110) >> 1);
    }

    public MqttPublishControlPacket setMqttQoS(MqttQoS mqttQoS) {
        fixedHeaderFlags = (mqttQoS.get() << 1) & fixedHeaderFlags;
        return this;
    }

	public boolean isRetainFlag() {
		return (fixedHeaderFlags & 1) == 1;
	}

	public MqttPublishControlPacket setRetainFlag(boolean retainFlag) {
        fixedHeaderFlags = (retainFlag ? 1 : 0) | fixedHeaderFlags;
        return this;
	}

    @Override
    public MqttControlPacketType getType() {
        return MqttControlPacketType.PUBLISH;
    }

    @Override
    public int getFixedHeaderFlags() {
        return fixedHeaderFlags;
    }

    public MqttPublishControlPacket setFixedHeaderFlags(int fixedHeaderFlags) {
        this.fixedHeaderFlags = fixedHeaderFlags;
        return this;
    }

    public int getPacketIdentifier() {
        return packetIdentifier;
    }

    public MqttPublishControlPacket setPacketIdentifier(int packetIdentifier) {
        this.packetIdentifier = packetIdentifier;
        return this;
    }

    public String getPayload() {
        return payload;
    }

    public MqttPublishControlPacket setPayload(String payload) {
        this.payload = payload;
        return this;
    }

    public String getTopic() {
        return topic;
    }

    public MqttPublishControlPacket setTopic(String topic) {
        this.topic = topic;
        return this;
    }


    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("- Mqtt Publish Control Packet" + "\n");
        builder.append("DUP-flag: " + isDUPFlag() + "\n");
        builder.append("Retain-flag: " + isRetainFlag() + "\n");
        builder.append("QoS: " + getMqttQoS() + "\n");
        builder.append("Packet identifier: " + getPacketIdentifier() + "\n" );
        builder.append("Payload: " + getPayload() + "\n" );
        builder.append("Topic: " + getTopic() + "\n" );
        return builder.toString();
    }

}
