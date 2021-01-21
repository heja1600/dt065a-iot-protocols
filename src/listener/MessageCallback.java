package listener;

public interface MessageCallback<Message> {
    void send(Message message);
}   
