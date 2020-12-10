




package server.listener;


public interface ServerListener<Message> {
    public void onMessageReceived(Message message, MessageCallback<Message> callback);
}