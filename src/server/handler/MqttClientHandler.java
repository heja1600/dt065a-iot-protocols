package server.handler;

import java.util.HashMap;

import model.mqtt.MqttClient;

public class MqttClientHandler {

    HashMap<String, MqttClient> clients;
    
    public MqttClientHandler() {
        clients = new HashMap<>();
    }

    public void addClient() {
        
    }

    public void removeClient( ){

    }

    public MqttClient getClient() {
        return null;
    }
}
