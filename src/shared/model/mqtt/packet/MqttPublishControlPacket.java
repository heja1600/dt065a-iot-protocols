package shared.model.mqtt.packet;

import shared.model.mqtt.MqttConnectFlag;
import shared.model.mqtt.MqttControlPacketType;
import shared.model.mqtt.MqttQoS;

public class MqttPublishControlPacket extends AbstractMqttControlPacket {
    private Integer qos;
    private String topic;
    private MqttConnectFlag connectFlag;

    public MqttPublishControlPacket() {
        super(MqttControlPacketType.PUBLISH);
        qos = 0;
        topic = "HEJ";
        connectFlag = new MqttConnectFlag();
    }

    public MqttConnectFlag getPublishFlag() {
        if (this.connectFlag == null) {
            this.connectFlag = new MqttConnectFlag().setDupFlag().setMqttQoS(MqttQoS.AT_MOST_ONCE);

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

    public Integer getQos() {
        return qos;
    }

    public MqttPublishControlPacket setQos(Integer qos) {
        this.qos = qos;
        return this;
    }

}
