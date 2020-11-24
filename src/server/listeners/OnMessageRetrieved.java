




package server.listeners;

import shared.model.CoapMessage;

public interface OnMessageRetrieved {
    public void onMessageRetrieved(CoapMessage message);
}