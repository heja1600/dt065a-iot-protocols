package model.mqtt;

import model.mqtt.packet.AbstractMqttControlPacket;

/** Implementation of MQTT 3.1.1 */

public class MqttMessage {

    private boolean DUPFlag;
    private boolean retainFlag;
    private MqttQoS mqttQoS;
    private Integer remainingLength;
    
    private AbstractMqttControlPacket packet;

    
    public MqttMessage() {
        DUPFlag = false;
        retainFlag = false;
        mqttQoS = MqttQoS.AT_MOST_ONCE;
    }
    
    public MqttControlPacketType getMqttControlPacketType() {
        return getPacket() == null ? null : getPacket().getType();
    }

    public boolean isDUPFlag() {
        return this.DUPFlag;
    }

    public boolean getDUPFlag() {
        return this.DUPFlag;
    }

    public MqttMessage setDUPFlag(boolean DUPFlag) {
        this.DUPFlag = DUPFlag;
        return this;
    }
    public MqttQoS getMqttQoS() {
        return this.mqttQoS;
    }

    public MqttMessage setMqttQoS(MqttQoS mqttQoS) {
        this.mqttQoS = mqttQoS;
        return this;
    }

	public boolean isRetainFlag() {
		return retainFlag;
	}

	public MqttMessage setRetainFlag(boolean retainFlag) {
        this.retainFlag = retainFlag;
        return this;
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
        builder.append("DUP-flag: " + isDUPFlag() + "\n");
        builder.append("Retain-flag: " + isRetainFlag() + "\n");
        builder.append("QoS:" + getMqttQoS() + "\n");
        builder.append("Remaining Length: " + getRemainingLength() + "\n");
        if(getPacket() != null) {
            builder.append(getPacket());
        }
        System.out.print("\n");
        return builder.toString();
    }
}
