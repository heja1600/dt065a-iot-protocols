




package listener;


public interface ServerListener<Message> {
    public void onMessageReceived(Message message, MessageReceiverCallback<Message> callback);
}