package server.listener;

public interface MessageCallback<Message> {
    void respond(Message message);
}   
