package listener;

public interface ExtendedServerListener <Message> {
    public void onMessageReceived(Message message);
    public void onMessageSent(Message message);
}