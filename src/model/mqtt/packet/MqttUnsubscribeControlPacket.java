package model.mqtt.packet;

import java.util.ArrayList;

import model.mqtt.MqttControlPacketType;
import model.mqtt.MqttPacketIdentifier;

public class MqttUnsubscribeControlPacket extends AbstractMqttControlPacket implements MqttPacketIdentifier {

    private ArrayList<String> topics;
    private int packetIdentifier;

    public MqttUnsubscribeControlPacket() {
        topics = new ArrayList<>();
    }
    
    @Override
    public int getPacketIdentifier() {
        return packetIdentifier;
    }

    public MqttUnsubscribeControlPacket setPacketIdentifier(int packetIdentifier) {
        this.packetIdentifier = packetIdentifier;
        return this;
    }

    public ArrayList<String> getTopics() {
        return topics;
    }

    public MqttUnsubscribeControlPacket addTopic(String topic) {
        if(topics == null) {
            topics = new ArrayList<>();
        }
        topics.add(topic);
        return this;
    }

    public MqttUnsubscribeControlPacket setQoSs(ArrayList<String> topics) {
        this.topics = topics;
        return this;
    }
	@Override
	public int getFixedHeaderFlags() {
		// TODO Auto-generated method stub
		return 0b0010;
    }

    @Override
	public MqttControlPacketType getType() {
		return MqttControlPacketType.UNSUBSCRIBE;
	}

    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("- Mqtt Unsubscribe Control Packet" + "\n");
        builder.append("Packet identifier: " + getPacketIdentifier() + "\n");
        this.topics.forEach(topic -> {
            builder.append("Topic: " + topic + "\n");
        });
        System.out.print("\n");
        return builder.toString();
    }
}
