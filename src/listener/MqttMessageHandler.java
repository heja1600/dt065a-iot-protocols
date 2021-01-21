package listener;

import model.mqtt.MqttClient;

public interface MqttMessageHandler {
    public void onClientConnect(MqttClient client);
}
