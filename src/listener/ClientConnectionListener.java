package listener;

public interface ClientConnectionListener<Message> {
    void receivePacket(Callback<Message> callback);
    void close();
    void send(Message message);
}
