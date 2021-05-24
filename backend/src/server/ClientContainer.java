package src.server;

import java.util.HashMap;

import src.model.mqtt.MqttClient;
import src.model.mqtt.MqttMessage;

public class ClientContainer {

    private HashMap<String, MqttClient> clients;
    private HashMap<Integer, String> clientIdMap;

    
    public ClientContainer() {
        this.clients = new HashMap<>();
        this.clientIdMap = new HashMap<>();

    }

    public void addClient(Integer clientIndex, MqttClient client) {
        this.clientIdMap.put(clientIndex, client.getClientIdentifier());
        this.clients.put(client.getClientIdentifier(), client);
    }

    public String getClientIdentifier(int clientIndex) {
        return this.clientIdMap.get(clientIndex);
    }
    public MqttClient getClient(int clientIndex) {
        return clients.get(getClientIdentifier(clientIndex));
    }

    public void removeClient(int clientIndex) {
        String clientIdentifier = getClientIdentifier(clientIndex);
        MqttClient client = clients.get(clientIdentifier);
        client.closeConnection();
        clients.remove(clientIdentifier); 
        clientIdMap.remove(clientIndex);
    }

    public void sendMessage(String clientIdentifier, MqttMessage message) {
        clients.forEach((key, client) -> {
            if(client.getClientIdentifier().equals(clientIdentifier)) {
                client.sendMessage(message);
            }
        });
    }
    
}
