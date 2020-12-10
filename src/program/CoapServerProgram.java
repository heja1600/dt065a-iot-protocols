package program;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import server.CoapServer;
import server.service.TCPMessageReceiver;
import server.service.UDPMessageReceiver;
import shared.model.coap.CoapMessage;
import shared.service.CoapMessageParser;

public class CoapServerProgram {
    CoapServer<?> server;
    BufferedReader reader;
    public static void main(String [] args) {
        new CoapServerProgram();
    }
    @SuppressWarnings("unchecked")
    CoapServerProgram() {
        reader = new BufferedReader(new InputStreamReader(System.in));
        server = getServer();

        startProgram();
    }


    CoapServer<?> getServer() {
        while (true) {
            try {
                System.out.println("\n Choose communication type \n 1. UDP \n 2. TCP \n");
                int index = Integer.parseInt(reader.readLine());
                switch(index) {
                    case 1: {
                        return new CoapServer<UDPMessageReceiver<CoapMessage>>(new UDPMessageReceiver<>(new CoapMessageParser()));
                    }
                    case 2: {
                        return new CoapServer<TCPMessageReceiver<CoapMessage>>(new TCPMessageReceiver<>(new CoapMessageParser()));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    

    public void startProgram() {
        server.startServer();
    }
}