package server;



import config.ServerConfig;
import listener.ClientConnectListener;
import listener.ClientConnectionListener;
import listener.ServerListener;
import model.mqtt.MqttMessage;
import model.mqtt.packet.MqttConnAckControlPacket;
import model.mqtt.packet.MqttConnAckControlPacket.MqttConnectReturnCode;
import model.mqtt.packet.MqttConnectControlPacket;
import parser.MqttMessageParser;
import server.handler.MqttMessageHandler;
import server.receiver.TCPMessageReceiver;

public class MQTTBrokerServer implements ClientConnectListener<MqttMessage> {

    TCPMessageReceiver<MqttMessage> messageReceiver;
    ServerListener<MqttMessage> serverListener;
    MqttMessageHandler serverHandler;

    public MQTTBrokerServer() {
        serverHandler = new MqttMessageHandler();
        messageReceiver = new TCPMessageReceiver<>(new MqttMessageParser()).setListener(this).setPacketLength(1024)
                .setPort(ServerConfig.MQTT_SERVER_PORT);
        startServer();
    }

    public void startServer() {
        this.messageReceiver.start();
    }

    @Override
    public void onClientConnect(ClientConnectionListener<MqttMessage> connection) {
        connection.receivePacket(message -> {
            if (this.serverListener != null) {
                this.serverListener.onMessageReceived(message);
            }

            try {

                System.out.println("@ <- Received packet: \n" + message);
                MqttMessage response = new MqttMessage();
    
                switch (message.getMqttControlPacketType()) {
                    case CONNECT: {
                        MqttConnectControlPacket requestPacket = ((MqttConnectControlPacket) message.getPacket());
    
                        MqttConnAckControlPacket responsePacket = new MqttConnAckControlPacket()
                                .setMqttConnectReturnCode(MqttConnectReturnCode.CONNECTION_ACCEPTED)
                                .setSessionPresentFlag(!requestPacket.getConnectFlag().isCleanSessionFlag());
    
                        response.setPacket(responsePacket);
                        break;
                    }
                    case CONNACK:
                        break;
                    case DISCONNECT:
                        break;
                    case PINGREQ:
                        break;
                    case PINGRESP:
                        break;
                    case PUBLISH:
                        break;
                    case SUBCRIBE:
                        break;
                    case UNSUBSCRIBE:
                        break;
                    default:
                        throw new Exception("MQTT Message Handler does not support: " + message.getMqttControlPacketType());
                }
    
                System.out.println("@ -> Sending back packet: \n" + response);
    
                if (this.serverListener != null) {
                    this.serverListener.onMessageSent(response);
                }
    
                connection.send(response);
            } catch (Exception e) {
                connection.close();
            }
        });
    }
}
