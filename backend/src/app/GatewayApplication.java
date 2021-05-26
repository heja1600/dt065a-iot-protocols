package src.app;

import src.config.ServerConfig;
import src.listener.MqttClientListener;
import src.model.coap.CoapCode;
import src.model.coap.CoapMessage;
import src.model.coap.option.CoapOptionUriPath;
import src.model.mqtt.MqttControlPacketType;
import src.model.mqtt.MqttMessage;
import src.model.mqtt.packet.MqttPublishControlPacket;
import src.server.CoapServer.ServerType;
import src.util.MeasurementLogger;
import src.server.MqttBrokerServer;

public class GatewayApplication extends Thread implements MqttClientListener{

    String coapHostname = "192.168.43.75"; 
    String mqttHostname = "localhost";
    MqttBrokerServer mqttServer;
    CoapClientApplication coapClientApplication;
    SensorProgram sensorProgram;
    MqttClientApplication mqttClient;
    
    boolean runClient = true;
    boolean runGatewayApplication = true;
    MeasurementLogger mqttClientMqttBroker = new MeasurementLogger("mqttclient_mqttbroker.txt", 1);
    MeasurementLogger coapClientCoapServer = new MeasurementLogger("coapclient_coapserver.txt", 1);
    public static void main(String [] args ) throws Exception {
        new GatewayApplication();
    }
 
    public GatewayApplication() throws Exception {
        mqttServer = new MqttBrokerServer();
        coapClientApplication = new CoapClientApplication(coapHostname, ServerConfig.COAP_SERVER_PORT, ServerType.UDP);
        // sensorProgram = new SensorProgram(); // ska ej vara här egentligen
        start();
    }

    @Override
    public void onMqttClientPublish(String topic, String payload, MqttMessage message) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onMqttClientMessageReceived(MqttMessage message) {
        if(message.getMqttControlPacketType() == MqttControlPacketType.PINGRESP) {
            mqttClientMqttBroker.endClock();
        }
    }


    @Override
    public void run() {
        mqttClient = new MqttClientApplication(mqttHostname, ServerConfig.MQTT_SERVER_PORT).setListener(this).connect();
        try {
            // mqttClient.subscribe("pi/humidity");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
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


                        coapClientCoapServer.startClock();
                        coapClientApplication.sendMessage(
                             new CoapMessage().setCode(CoapCode.GET).addOption(new CoapOptionUriPath("pi")).addOption(new CoapOptionUriPath("humidity")),
                            response -> {
                                coapClientCoapServer.endClock();
                                if(response.getCode() == CoapCode.VALID) {
                                    System.out.println("response: " + response);
                                  
                                    try {
                                        mqttClientMqttBroker.startClock();
                                        mqttClient.publish("pi/humidity", response.getPayload());
                                    } catch (Exception e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                    }
                                } else {
                                    System.out.println("received bad response from sensor program");
                                }
                            });

                        for(String messageUri: new String[]{"pi/temperature", "pi/time",}) {
                            CoapMessage coapMessage =  new CoapMessage()
                                .setCode(CoapCode.GET);

                            for(String subMessageUri: messageUri.split("/")) {
                                coapMessage.addOption(new CoapOptionUriPath(subMessageUri));
                            }

                            coapClientApplication.sendMessage(
                                coapMessage,
                                response -> {
                                    if(response.getCode() == CoapCode.VALID) {
                                        System.out.println("response: " + response);
                                      
                                        try {
                                            mqttClient.publish(messageUri, response.getPayload());
                                        } catch (Exception e) {
                                            // TODO Auto-generated catch block
                                            e.printStackTrace();
                                        }
                                    } else {
                                        System.out.println("received bad response from sensor program");
                                    }
                                });
                        }
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
           }).start();
        
    }

    @Override
    public void onMqttClientMessageSent(MqttMessage message) {
        if(message.getMqttControlPacketType() == MqttControlPacketType.PINGREQ) {
            mqttClientMqttBroker.startClock();
        }
        // TODO Auto-generated method stub
        
    }
}



