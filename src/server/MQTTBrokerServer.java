package server;

import server.listener.ExtendedServerListener;
import server.listener.MessageCallback;
import server.listener.ServerListener;
import server.service.MqttMessageHandler;
import server.service.UDPMessageReceiver;
import shared.config.ServerConfig;
import shared.model.mqtt.MqttMessage;
import shared.service.MqttMessageParser;

public class MQTTBrokerServer implements ServerListener<MqttMessage<?>> {

    UDPMessageReceiver<MqttMessage<?>> messageReceiver;
    ExtendedServerListener<MqttMessage<?>> serverListener;
    MqttMessageHandler serverHandler;
    public MQTTBrokerServer() {
        serverHandler = new MqttMessageHandler();
        messageReceiver = new UDPMessageReceiver<>(new MqttMessageParser())
            .setListener(this)
            .setPacketLength(1024)
            .setPort(ServerConfig.MQTT_SERVER_PORT);
        startServer();
    }

    public void startServer() {
        this.messageReceiver.start();
    }

    @Override
    public void onMessageReceived(MqttMessage<?> message, MessageCallback<MqttMessage<?>> callback) {
        if(this.serverListener != null){
            this.serverListener.onMessageReceived(message);
        }

        MqttMessage<?> responseMessage = serverHandler.handleMessage(message);

        if(this.serverListener != null){
            this.serverListener.onMessageSent(responseMessage);
        }

        callback.respond(serverHandler.handleMessage(responseMessage));
    }
}
