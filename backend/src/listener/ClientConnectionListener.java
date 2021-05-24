package src.listener;

public interface ClientConnectionListener<Message> extends ClientSendEmitter<Message> {
    void receivePacket(UniformCallback<Message> callback);
    void close();
}
