package src.listener;

public interface ClientSendEmitter<Message> {
    void send(Message message);
}
