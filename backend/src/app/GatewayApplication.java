package src.app;

import src.config.ServerConfig;
import src.listener.MqttClientListener;
import src.model.coap.CoapCode;
import src.model.coap.CoapMessage;
import src.model.coap.option.CoapOptionUriPath;
import src.model.mqtt.MqttMessage;
import src.server.CoapServer.ServerType;
import src.server.MqttBrokerServer;

public class GatewayApplication extends Thread implements MqttClientListener{

    String hostname = "localhost";
    MqttBrokerServer mqttServer;
    CoapClientApplication coapClientApplication;
    SensorProgram sensorProgram;
    MqttClientApplication mqttClient;
    
    boolean runClient = true;
    boolean runGatewayApplication = true;
    
    public static void main(String [] args ) throws Exception {
        new GatewayApplication();
    }
 
    public GatewayApplication() throws Exception {
        mqttServer = new MqttBrokerServer();
        coapClientApplication = new CoapClientApplication(hostname, ServerConfig.COAP_SERVER_PORT, ServerType.UDP);
        sensorProgram = new SensorProgram(); // ska ej vara här egentligen
        start();
    }

    @Override
    public void onMqttClientPublish(String topic, String payload, MqttMessage message) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onMqttClientMessage(MqttMessage message) {
        // TODO Auto-generated method stub
        
    }


    @Override
    public void run() {
        mqttClient = new MqttClientApplication(hostname, ServerConfig.MQTT_SERVER_PORT).setListener(this).connect();
        while(runGatewayApplication) {
            
        }   
        // TODO Auto-generated method stub
        super.run();
    }

    @Override
    public void onMqttClientConnect() {
        // TODO Auto-generated method stub
        
        (new Thread() {
            @Override
            public void run() {
                while(runClient) {
                    try {
                        sleep(2500);
                        coapClientApplication.sendMessage(
                            new CoapMessage()
                                .setCode(CoapCode.GET)
                                .addOption(new CoapOptionUriPath("pi"))
                                .addOption(new CoapOptionUriPath("time")), 
                            response -> {
                                System.out.println("response: " + response);
                                
                                try {
                                    mqttClient.publish("pi/time", response.getPayload());
                                } catch (Exception e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                            });
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
           }).start();
        
    }
}



