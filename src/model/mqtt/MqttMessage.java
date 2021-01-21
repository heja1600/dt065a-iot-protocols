package model.mqtt;

import model.mqtt.packet.AbstractMqttControlPacket;

/** Implementation of MQTT 3.1.1 */

public class MqttMessage {

    private Integer remainingLength;
    
    private AbstractMqttControlPacket packet;

    
    public MqttMessage() {
 
    }
    
    public MqttControlPacketType getMqttControlPacketType() {
        return getPacket() == null ? null : getPacket().getType();
    }


	public Integer getRemainingLength() {
		return remainingLength;
	}

	public MqttMessage setRemainingLength(Integer remainingLength) throws Exception {
        if(remainingLength < 0x0 || remainingLength > 0xffff) {
            throw new Exception("Remaining length has to be between 1-4 bytes");
        }
        this.remainingLength = remainingLength;
        return this;
	}

    public AbstractMqttControlPacket getPacket() {
        return packet;
    }

    public MqttMessage setPacket(AbstractMqttControlPacket packet) {
        this.packet = packet;
        return this;
    }


    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("- MqttMessage" + "\n");
        builder.append("Control packet type: " + getMqttControlPacketType() + "\n");
        builder.append("Remaining Length: " + getRemainingLength() + "\n");
        if(getPacket() != null) {
            builder.append(getPacket());
        }
        System.out.print("\n");
        return builder.toString();
    }
}
