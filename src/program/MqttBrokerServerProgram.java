package program;

import server.MQTTBrokerServer;

public class MqttBrokerServerProgram {

    MQTTBrokerServer server;
    public static void main( String [] args){
        new MqttBrokerServerProgram();
    }

    public MqttBrokerServerProgram(){
        server = new MQTTBrokerServer();
        
    }
}
