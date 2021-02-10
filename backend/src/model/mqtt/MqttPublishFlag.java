package model.mqtt;

import util.ByteUtil;

public class MqttPublishFlag {
    boolean dupFlag = false;
    MqttQoS mqttQoS = MqttQoS.AT_MOST_ONCE;
    boolean retain = false;

    public int get() {
        return ByteUtil.boolArrayToInteger(dupFlag, (mqttQoS.get() & 2) == 2, (mqttQoS.get() & 1) == 1, retain);
    }

    public boolean isDupFlag() {
        return dupFlag;
    }

    public MqttPublishFlag setDupFlag() {
        this.dupFlag = true;
        return this;
    }

    public MqttQoS getMqttQoS() {
        return mqttQoS;
    }

    public MqttPublishFlag setMqttQoS(MqttQoS mqttQoS) {
        this.mqttQoS = mqttQoS;
        return this;
    }

    public boolean getRetain() {
        return retain;
    }

    public MqttPublishFlag setRetain() {
        this.retain = true;
        return this;
    }
}
