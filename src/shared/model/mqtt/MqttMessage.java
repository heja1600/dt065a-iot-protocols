package shared.model.mqtt;

import shared.model.mqtt.packet.AbstractMqttControlPacket;

/** Implementation of MQTT 3.1.1 */

public class MqttMessage {

    private MqttControlPacketType mqttControlPacketType;
    private boolean DUPFlag;
    private boolean retainFlag;
    private MqttQoS mqttQoS;
    private Integer remainingLength;
    
    private AbstractMqttControlPacket packet;

    
    public MqttMessage() {
        DUPFlag = false;
        mqttControlPacketType = null;
        retainFlag = false;
        mqttQoS = MqttQoS.AT_MOST_ONCE;
    }
    
    public MqttControlPacketType getMqttControlPacketType() {
        return this.mqttControlPacketType;
    }

    public MqttMessage setMqttControlPacketType(MqttControlPacketType mqttControlPacketType) {
        this.mqttControlPacketType = mqttControlPacketType;
        return this;
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
        if(remainingLength < 0x0 || remainingLength > 0x4) {
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
        // TODO Auto-generated method stub
        return super.toString();
    }
}
