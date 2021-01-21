package server;

import config.ServerConfig;
import listener.ExtendedServerListener;
import listener.MessageReceiverCallback;
import listener.ServerListener;
import model.mqtt.MqttMessage;
import parser.MqttMessageParser;
import server.handler.MqttMessageHandler;
import server.receiver.TCPMessageReceiver;

public class MQTTBrokerServer implements ServerListener<MqttMessage> {

    TCPMessageReceiver<MqttMessage> messageReceiver;
    ExtendedServerListener<MqttMessage> serverListener;
    MqttMessageHandler serverHandler;
    public MQTTBrokerServer() {
        serverHandler = new MqttMessageHandler();
        messageReceiver = new TCPMessageReceiver<>(new MqttMessageParser())
            .setListener(this)
            .setPacketLength(1024)
            .setPort(ServerConfig.MQTT_SERVER_PORT);
        startServer();
    }

    public void startServer() {
        this.messageReceiver.start();
    }

    @Override
    public void onMessageReceived(MqttMessage message, MessageReceiverCallback<MqttMessage> callback) {
        if(this.serverListener != null){
            this.serverListener.onMessageReceived(message);
        }
        try {
            MqttMessage responseMessage = serverHandler.handleMessage(message);

            if(this.serverListener != null){
                this.serverListener.onMessageSent(responseMessage);
            }
            
            callback.respond(responseMessage);
        } catch(Exception e) {
            callback.close();
        }

    }
}
