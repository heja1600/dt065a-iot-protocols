package model.mqtt.packet;

import java.util.ArrayList;

import model.mqtt.MqttControlPacketType;
import model.mqtt.MqttPacketIdentifier;
import model.mqtt.MqttTopic;

public class MqttSubscribeControlPacket extends AbstractMqttControlPacket implements MqttPacketIdentifier {

    int packetIdentifier;
    ArrayList<MqttTopic> topics;
    
    public MqttSubscribeControlPacket() {
        this.topics = new ArrayList<>();
    }
    @Override
    public MqttControlPacketType getType() {
        return MqttControlPacketType.SUBCRIBE;
    }

    @Override
    public int getFixedHeaderFlags() {
        return 0b10;
    }

    public int getPacketIdentifier() {
        return packetIdentifier;
    }

    public MqttSubscribeControlPacket setPacketIdentifier(int packetIdentifier) {
        this.packetIdentifier = packetIdentifier;
        return this;
    }

    public ArrayList<MqttTopic> getTopics() {
        return topics;
    }

    public MqttSubscribeControlPacket setTopics(ArrayList<MqttTopic> topics) {
        this.topics = topics;
        return this;
    }

    public MqttSubscribeControlPacket addTopic(MqttTopic topic) {
        if(this.topics == null) {
            this.topics = new ArrayList<>();
        }
        this.topics.add(topic);
        return this;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("- Mqtt Subscribe Control Packet" + "\n");
        builder.append("Packet identifier: " + getPacketIdentifier() + "\n");
        this.topics.forEach(topic -> {
            builder.append("Topic: " + topic.getTopic() + ", QoS: " + topic.getMqttQoS() + "\n");
        });
        System.out.print("\n");
        return builder.toString();
    }
}
