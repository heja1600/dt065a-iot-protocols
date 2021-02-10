package listener;

public interface ServerListener <Message> {
    public void onMessageReceived(Message message);
    public void onMessageSent(Message message);
}