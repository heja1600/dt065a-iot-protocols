package shared.model.mqtt;

public class MqttMessage {

    private MqttMessageType mqttMessageType;
    private boolean DUPFlag;
    private boolean retainFlag;
    private MqttQoS mqttQoS;
    private Integer remainingLength;

    public MqttMessage() {
        DUPFlag = false;
        mqttMessageType = null;
        retainFlag = false;
        mqttQoS = MqttQoS.AT_MOST_ONCE;
    }
    
    public MqttMessageType getMqttMessageType() {
        return this.mqttMessageType;
    }

    public MqttMessage setMqttMessageType(MqttMessageType mqttMessageType) {
        this.mqttMessageType = mqttMessageType;
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
}
