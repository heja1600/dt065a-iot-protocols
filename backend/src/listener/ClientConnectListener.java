package listener;

public interface ClientConnectListener<Message> {
    public void onClientConnect(ClientConnectionListener<Message> connection);
}
