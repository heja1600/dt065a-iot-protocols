package listener;

public interface ClientSendEmitter<Message> {
    void send(Message message);
}
