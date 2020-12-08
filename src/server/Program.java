package server;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import server.service.TCPMessageHandler;
import server.service.UDPMessageHandler;

public class Program {
    Server<?> server;
    BufferedReader reader;
    public static void main(String [] args) {
        new Program();
    }
    @SuppressWarnings("unchecked")
    Program() {
        reader = new BufferedReader(new InputStreamReader(System.in));
        server = new Server(getServerType());
        startProgram();
    }

    Class<?> getServerType() {
        while (true) {
            try {
                System.out.println("\n Choose communication type \n 1. UDP \n 2. TCP \n");
                int index = Integer.parseInt(reader.readLine());
                switch(index) {
                    case 1: {
                        return UDPMessageHandler.class;
                    }
                    case 2: {
                        return TCPMessageHandler.class;
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