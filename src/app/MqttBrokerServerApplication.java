package app;

import server.MQTTBrokerServer;

public class MqttBrokerServerApplication {

    MQTTBrokerServer server;
    public static void main( String [] args){
        new MqttBrokerServerApplication();
    }

    public MqttBrokerServerApplication(){
        server = new MQTTBrokerServer();
        
    }
}
