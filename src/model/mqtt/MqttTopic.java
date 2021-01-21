package model.mqtt;

public class MqttTopic {
  
    private String topic;
    private MqttQoS mqttQoS;

    public MqttTopic(String topic, MqttQoS mqttQoS) {
        this.topic = topic;
        this.mqttQoS = mqttQoS;
    }

    public MqttTopic() {

    }

    public String getTopic() {
        return topic;
    }

    public MqttTopic setTopic(String topic) {
        this.topic = topic;
        return this;
    }

    public MqttQoS getMqttQoS() {
        return mqttQoS;
    }

    public MqttTopic setMqttQoS(MqttQoS mqttQoS) {
        this.mqttQoS = mqttQoS;
        return this;
    }

}