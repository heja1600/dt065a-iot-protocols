package model.mqtt.packet;

import java.util.ArrayList;

import model.mqtt.MqttControlPacketType;
import model.mqtt.MqttPacketIdentifier;
import model.mqtt.MqttQoS;

public class MqttSubAckControlPacket extends AbstractMqttControlPacket implements MqttPacketIdentifier {

    private int packetIdentifier;

    private ArrayList<MqttQoS> QoSs;
    
    public MqttSubAckControlPacket() {
        this.QoSs = new ArrayList<>();
    }

    @Override
    public MqttControlPacketType getType() {
        return MqttControlPacketType.SUBACK;
    }

    @Override
    public int getFixedHeaderFlags() {
        return 0;
    }

    public int getPacketIdentifier() {
        return packetIdentifier;
    }

    public MqttSubAckControlPacket setPacketIdentifier(int packetIdentifier) {
        this.packetIdentifier = packetIdentifier;
        return this;
    }

    public ArrayList<MqttQoS> getQoSs() {
        return QoSs;
    }

    public MqttSubAckControlPacket addQoS(MqttQoS mqttQoS) {
        if(this.QoSs == null) {
            this.QoSs = new ArrayList<>();
        }
        this.QoSs.add(mqttQoS);
        return this;
    }

    public MqttSubAckControlPacket setQoSs(ArrayList<MqttQoS> qoSs) {
        QoSs = qoSs;
        return this;
    }
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("- Mqtt Sub Ack Packet" + "\n");
        builder.append("Packet Identifier: " + getPacketIdentifier() + "\n");
        builder.append("Packet QoSs: " + getQoSs() + "\n");
        System.out.print("\n");
        return builder.toString();
    }
}
