package src.listener;

import src.model.mqtt.MqttMessage;

public interface MqttClientListener {
    void onMqttClientPublish(String topic, String payload, MqttMessage message);
    void onMqttClientMessageReceived(MqttMessage message);
    void onMqttClientConnect();
    void onMqttClientMessageSent(MqttMessage message);
}
