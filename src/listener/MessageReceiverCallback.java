package listener;

public interface MessageReceiverCallback<Message> extends MessageCallback<Message>{
    void close();
}
