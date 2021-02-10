package app;

import server.MqttBrokerServer;

public class MqttBrokerServerApplication {

    MqttBrokerServer server;
    
    public static void main(String [] args){
        new MqttBrokerServerApplication();
    }

    public MqttBrokerServerApplication(){
        server = new MqttBrokerServer();
    }
}
