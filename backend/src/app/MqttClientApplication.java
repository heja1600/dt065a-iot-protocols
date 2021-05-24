package src.app;

import src.config.ServerConfig;
import src.listener.ClientConnectListener;
import src.listener.ClientConnectionListener;
import src.listener.MqttClientListener;
import src.model.mqtt.MqttConnectFlag;
import src.model.mqtt.MqttMessage;
import src.model.mqtt.MqttQoS;
import src.model.mqtt.MqttTopic;
import src.model.mqtt.packet.MqttConnectControlPacket;
import src.model.mqtt.packet.MqttPingRequestPacket;
import src.model.mqtt.packet.MqttPublishControlPacket;
import src.model.mqtt.packet.MqttSubscribeControlPacket;
import src.parser.MqttMessageParser;
import src.server.receiver.TCPClientMessageReceiver;

public class MqttClientApplication implements ClientConnectListener<MqttMessage> {

    MqttMessageParser parser;
    MqttClientListener listener;
    boolean clientRun = true;
    String hostname;
    Integer port;
    TCPClientMessageReceiver<MqttMessage> messageReceiver;
    ClientConnectionListener<MqttMessage> connection;
    public static void main(String [] args) throws InterruptedException {
        final MqttClientApplication app = new MqttClientApplication("localhost", ServerConfig.MQTT_SERVER_PORT).connect();
        app.setListener(new MqttClientListener(){

            @Override
            public void onMqttClientPublish(String topic, String payload, MqttMessage message) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void onMqttClientMessage(MqttMessage message) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void onMqttClientConnect() {
                try {
                    app.publish("hej", "2021/05/24 14:53:58");
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                // TODO Auto-generated method stub
            }
            
        });
    }

    public MqttClientApplication(String hostname, Integer port) {
        this.hostname = hostname;
        this.port = port;
        parser = new MqttMessageParser();

    }


    public MqttClientApplication setListener(MqttClientListener listener) {
        this.listener = listener;
        return this;
    }

    public MqttClientApplication connect() {

        messageReceiver = new TCPClientMessageReceiver<>(parser).setHostname(hostname).setPort(port).setPacketLength(1024).setListener(this);
        messageReceiver.start();

        return this;
        
    }
    public void subscribe(String topic) throws Exception {
        if(connection == null) {
            throw new Exception("Connect to server before subscribing");
        }
        MqttMessage message =  new MqttMessage().setPacket(
            new MqttSubscribeControlPacket().addTopic(new MqttTopic().setTopic(topic))
        );

        connection.send(message);
    }

    public void publish(String topic, String payload) throws Exception {
        if(connection == null) {
            throw new Exception("Connect to server before publishing");
        }
        if(topic == null || payload == null) {
            throw new Exception("Payload or topic cannot be null");
        }
        
        MqttMessage message = new MqttMessage().setPacket(
            new MqttPublishControlPacket().setPayload(payload).setTopic(topic).setMqttQoS(MqttQoS.AT_MOST_ONCE)
        );
        

        connection.send(message);
    }

    public void ping() {
        connection.send(new MqttMessage().setPacket(new MqttPingRequestPacket()));
    }

    @Override
    public void onClientConnect(ClientConnectionListener<MqttMessage> connection) {
        this.connection = connection;
        connection.receivePacket(message -> {
            switch(message.getMqttControlPacketType()) {
                case CONNACK:
                    if(listener != null) {
                        ping();
                        listener.onMqttClientConnect();
                    }
                    break;
                case CONNECT:
                    break;
                case DISCONNECT:
                    break;
                case PINGREQ:
                    break;
                case PINGRESP: {
                    new Thread(new Runnable(){
                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            try {
                                Thread.sleep(10000);
                                ping();
                            } catch (InterruptedException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                        
                    }).start();
                    break;
                }
                case PUBACK:
                    break;
                case PUBLISH: {
                    MqttPublishControlPacket packet = (MqttPublishControlPacket) message.getPacket();
                    if(listener != null) {
                        listener.onMqttClientPublish(packet.getTopic(), packet.getPayload(), message);
                    }
                    break;
                }
                case SUBACK:
                    break;
                case SUBCRIBE:
                    break;
                case UNSUBACK:
                    break;
                case UNSUBSCRIBE:
                    break;
                default:
                    break;
            }
            if(listener != null) {
                listener.onMqttClientMessage(message);
            }

        }); 
        MqttMessage connectMessage = new MqttMessage().setPacket(
            new MqttConnectControlPacket()
            .setClientIdentifier("MQTTBOT")
            .setConnectFlag(new MqttConnectFlag().setCleanSessionFlag())
        );
        connection.send(connectMessage);
    }
}
