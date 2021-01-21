package service;

import java.util.ArrayList;
import java.util.HashMap;

import model.mqtt.MqttClient;
import model.mqtt.MqttTopic;
import server.ClientContainer;

public class MqttTopicService {

    HashMap<String, ArrayList<MqttClient>> topics;
    ClientContainer clientContainer;

    public MqttTopicService(ClientContainer clientContainer) {
        this.clientContainer = clientContainer;
        this.topics = new HashMap<>();
    }
    public void addTopic(MqttTopic topic, Integer clientIndex) 
    {
        if(!topics.containsKey(topic.getTopic())) {     
            topics.put(topic.getTopic(), new ArrayList<>());
        }

        topics.get(topic.getTopic()).add(clientContainer.getClient(clientIndex));
    }

    public void addTopic(ArrayList<MqttTopic> topics, Integer clientIndex) {
        topics.forEach(topic -> {
            addTopic(topic, clientIndex);
        });
    }

    public ArrayList<MqttClient> getClients(String topic) {
        if(topics.containsKey(topic)) {
            return topics.get(topic);
        }
        return new ArrayList<>();
    }

    public void removeTopic(ArrayList<String> topics, Integer clientIndex) {
        topics.forEach(topic -> {
            removeTopic(topic, clientIndex);
        });
    }
    public void removeTopic(String topic, Integer clientIndex) {
        if(!topics.containsKey(topic)) {
            return;
        }
        ArrayList<MqttClient> clients = getClients(topic);
        for(var i = 0; i < clients.size(); i++) {
            if(clients.get(i).getClientIdentifier().equals(clientContainer.getClientIdentifier(clientIndex))) {
                clients.remove(i);
                return;
            }
        }
    }

    public void removeUser(Integer clientIndex) {
        topics.forEach((topic, clients) -> {
            for(var i = 0; i < clients.size(); i++) {
                if(clients.get(i).getClientIdentifier().equals(clientContainer.getClientIdentifier(clientIndex))) {
                    clients.remove(i);
                    return;
                }
            }
        });
    }
}
