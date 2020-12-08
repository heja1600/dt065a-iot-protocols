




package server.listeners;

import shared.model.coap.CoapMessage;

public interface ServerListener {
    public void onMessageReceived(CoapMessage message);
}