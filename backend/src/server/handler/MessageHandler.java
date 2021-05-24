package src.server.handler;

public interface MessageHandler<Message> {
    Message handleMessage(Message message);
}
