package model.mqtt;

import listener.ClientConnectionListener;

public class MqttClient {

    private String clientIdentifier;
    private ClientConnectionListener<MqttMessage> connection;
    
    public MqttClient(
        String clientIdentifier,
        ClientConnectionListener<MqttMessage> connection
    ) {
        this.clientIdentifier = clientIdentifier;
        this.connection = connection;
    }

    public void sendMessage(MqttMessage message) {
        connection.send(message);
    }
    
    public void closeConnection() {
        connection.close();
    }
    
    public String getClientIdentifier() {
        return clientIdentifier;
    }
}
