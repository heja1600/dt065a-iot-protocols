




package server.listeners;

import shared.model.coap.CoapMessage;

public interface OnMessageRetrieved {
    public void onMessageRetrieved(CoapMessage message);
}