package shared.model.mqtt;

import shared.model.mqtt.packet.AbstractMqttControlPacket;


/** Implementation of MQTT 3.1.1 */

public class MqttMessage<Packet extends AbstractMqttControlPacket> {

    private MqttControlPacketType mqttMessageType;
    private boolean DUPFlag;
    private boolean retainFlag;
    private MqttQoS mqttQoS;
    private Integer remainingLength;
    
    private Packet packet;

    
    public MqttMessage() {
        DUPFlag = false;
        mqttMessageType = null;
        retainFlag = false;
        mqttQoS = MqttQoS.AT_MOST_ONCE;
    }
    
    public MqttControlPacketType getMqttMessageType() {
        return this.mqttMessageType;
    }

    public MqttMessage<Packet> setMqttMessageType(MqttControlPacketType mqttMessageType) {
        this.mqttMessageType = mqttMessageType;
        return this;
    }

    public boolean isDUPFlag() {
        return this.DUPFlag;
    }

    public boolean getDUPFlag() {
        return this.DUPFlag;
    }

    public MqttMessage<Packet> setDUPFlag(boolean DUPFlag) {
        this.DUPFlag = DUPFlag;
        return this;
    }
    public MqttQoS getMqttQoS() {
        return this.mqttQoS;
    }

    public MqttMessage<Packet> setMqttQoS(MqttQoS mqttQoS) {
        this.mqttQoS = mqttQoS;
        return this;
    }

	public boolean isRetainFlag() {
		return retainFlag;
	}

	public MqttMessage<Packet> setRetainFlag(boolean retainFlag) {
        this.retainFlag = retainFlag;
        return this;
	}

	public Integer getRemainingLength() {
		return remainingLength;
	}

	public MqttMessage<Packet> setRemainingLength(Integer remainingLength) throws Exception {
        if(remainingLength < 0x0 || remainingLength > 0x4) {
            throw new Exception("Remaining length has to be between 1-4 bytes");
        }
        this.remainingLength = remainingLength;
        return this;
	}

    public Packet getPacket() {
        return packet;
    }

    public MqttMessage<Packet> setPacket(Packet packet) {
        this.packet = packet;
        return this;
    }
}
