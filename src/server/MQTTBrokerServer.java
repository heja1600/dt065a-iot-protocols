package server;



import config.ServerConfig;
import listener.ClientConnectListener;
import listener.ClientConnectionListener;
import listener.ServerListener;
import model.mqtt.MqttClient;
import model.mqtt.MqttMessage;
import model.mqtt.MqttQoS;
import model.mqtt.packet.MqttConnAckControlPacket;
import model.mqtt.packet.MqttConnAckControlPacket.MqttConnectReturnCode;
import model.mqtt.packet.MqttConnectControlPacket;
import model.mqtt.packet.MqttPingResponsePacket;
import model.mqtt.packet.MqttPubAckControlPacket;
import model.mqtt.packet.MqttPublishControlPacket;
import model.mqtt.packet.MqttSubAckControlPacket;
import model.mqtt.packet.MqttSubscribeControlPacket;
import model.mqtt.packet.MqttUnsubAckControlPacket;
import model.mqtt.packet.MqttUnsubscribeControlPacket;
import parser.MqttMessageParser;
import server.receiver.TCPMessageReceiver;
import service.MqttTopicService;
import util.ByteUtil;

public class MqttBrokerServer implements ClientConnectListener<MqttMessage> {

    TCPMessageReceiver<MqttMessage> messageReceiver;
    ServerListener<MqttMessage> serverListener;

    ClientContainer clientContainer;
    int totalConnections = 0;
    MqttTopicService topicService;
    public MqttBrokerServer() {
        clientContainer = new ClientContainer();
        topicService = new MqttTopicService(clientContainer);

        messageReceiver = new TCPMessageReceiver<>(new MqttMessageParser()).setListener(this).setPacketLength(1024)
                .setPort(ServerConfig.MQTT_SERVER_PORT);
        startServer();
    }

    public void startServer() {
        this.messageReceiver.start();
    }

    @Override
    public void onClientConnect(ClientConnectionListener<MqttMessage> connection) {
        MqttMessageParser parser = new MqttMessageParser();
        int clientIndex = getClientIndex();

        connection.receivePacket(request -> {
            if (this.serverListener != null) {
                this.serverListener.onMessageReceived(request);
            }

            try {
       
                System.out.println("@ <- Received packet: \n" + request);
                ByteUtil.printBytesAsString(parser.encode(request));

        
                MqttMessage response = new MqttMessage();
    
                switch (request.getMqttControlPacketType()) {
                    case CONNECT: {
                        MqttConnectControlPacket requestPacket = ((MqttConnectControlPacket) request.getPacket());
    
                        MqttConnAckControlPacket responsePacket = new MqttConnAckControlPacket()
                                .setMqttConnectReturnCode(MqttConnectReturnCode.CONNECTION_ACCEPTED)
                                .setSessionPresentFlag(!requestPacket.getConnectFlag().isCleanSessionFlag());
    
                        clientContainer.addClient(
                            clientIndex,
                            new MqttClient(
                                requestPacket.getClientIdentifier(), 
                                connection
                            )
                        );
                        
                        response.setPacket(responsePacket);
                        break;
                    }
                    case DISCONNECT: {
                        topicService.removeUser(clientIndex);
                        clientContainer.removeClient(clientIndex);
                        break;
                    }
                    case PINGREQ:
                        response.setPacket(new MqttPingResponsePacket());
                        break;
        
                    case PUBLISH: {
                        var publishRequest = (MqttPublishControlPacket) request.getPacket();

                        if(publishRequest.getMqttQoS() == MqttQoS.AT_LEAST_ONCE) {
                            /** Pub acc */
                            response.setPacket(
                                new MqttPubAckControlPacket()
                                    .setPacketIdentifier(publishRequest.getPacketIdentifier())
                            );
                        } else if(publishRequest.getMqttQoS() == MqttQoS.EXACLY_ONCE) {
                            /** Pub rec @TODO */
                        }

                        topicService.getClients(publishRequest.getTopic()).forEach(client -> {
                            client.sendMessage(request);
                        });
                        break;
                    }
                    case SUBCRIBE: {
                        var subscribeRequest = (MqttSubscribeControlPacket) request.getPacket();
                        response.setPacket(
                            new MqttSubAckControlPacket()
                                .setPacketIdentifier(subscribeRequest.getPacketIdentifier()
                            )
                        );
                        topicService.addTopic(subscribeRequest.getTopics(), clientIndex);
              
                        break;
                    }
                       
                    case UNSUBSCRIBE: {
                        var unsubscribeRequest = (MqttUnsubscribeControlPacket) request.getPacket();
                        response.setPacket(
                            new MqttUnsubAckControlPacket()
                                .setPacketIdentifier(unsubscribeRequest.getPacketIdentifier()
                            )
                        );
                        topicService.removeTopic(unsubscribeRequest.getTopics(), clientIndex);
                        break;
                    }
                    case PINGRESP:
                    case CONNACK:
                    default: {
                        throw new Exception("MQTT Message Handler does not support: " + request.getMqttControlPacketType());
                    }
                }
				
                // Send back packet if it exists
                if(response.getPacket() != null) { 
                    
                    System.out.println("@ -> Sending back packet: \n" + response);
                    if (this.serverListener != null) {
                        this.serverListener.onMessageSent(response);
                    }
                    try {
                        ByteUtil.printBytesAsString(parser.encode(response));
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
					
                    connection.send(response);
                }

            } catch (Exception e) {
                connection.close();
            }
        });
    }

    private int getClientIndex() {
        return ++this.totalConnections;
    }
}
