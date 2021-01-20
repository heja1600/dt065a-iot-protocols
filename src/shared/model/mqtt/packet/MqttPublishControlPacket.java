package shared.model.mqtt.packet;

import shared.model.mqtt.MqttControlPacketType;
import shared.model.mqtt.MqttPublishFlag;
import shared.model.mqtt.MqttQoS;

public class MqttPublishControlPacket extends AbstractMqttControlPacket {
    private boolean retain;
    private String topic;
    private MqttPublishFlag connectFlag;

    public MqttPublishControlPacket() {
        super(MqttControlPacketType.PUBLISH);
        retain = false;
        connectFlag = new MqttPublishFlag();
    }

    public MqttPublishFlag getPublishFlag() {
        if (this.connectFlag == null) {
            this.connectFlag = new MqttPublishFlag().setDupFlag().setMqttQoS(MqttQoS.AT_MOST_ONCE);

        }
        return this.connectFlag;

    }

    public String getTopic() {
        return topic;
    }

    public MqttPublishControlPacket setTopic(String topic) {
        this.topic = topic;
        return this;
    }

    public boolean getRetain() {
        return retain;
    }

    public MqttPublishControlPacket setRetain() {
        this.retain = true;
        return this;
    }

}
