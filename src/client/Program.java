package client;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import shared.config.ServerConfig;
import shared.model.CoapCode;
import shared.model.CoapMessage;

import shared.service.CoapMesssageParser;
import shared.util.ByteUtil;

public class Program {
    boolean runProgram = true;
    String hostname = "localhost";
    int port = ServerConfig.SERVER_PORT;
    BufferedReader reader;
    public static void main(String[] args) throws IOException {
        new Program();
    }

    Program() {
        reader = new BufferedReader(new InputStreamReader(System.in));
        startProgram();
    }


    void startProgram() {
        while(runProgram) {
            CoapMessage message = new CoapMessage();
            message.setCoapCode(getCoapCode());
            System.out.println(message.getCoapCode().get());
            message.setUriPath(getCoapUriPath());
            sendMessage(message);
        }
    }

    CoapCode getCoapCode() {
        while(true) {
            try {
                System.out.print("Choose CoAP method \n 1. GET \n 2. POST \n 3. PUT \n 4. DELETE \n");
                Integer inputCoapCode = Integer.parseInt(reader.readLine());
                switch(inputCoapCode) {
                    case 1: {
                        return CoapCode.GET;  
                    }
                    case 2: {
                        return CoapCode.POST;
                    } 
                    case 3: {
                        return CoapCode.PUT;
                    }
                    case 4: {
                        return CoapCode.DELETE;
                    }
                    default : {
                        System.out.println("Choose number between 1-4 \n");
                    }
                }

            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }


    String getCoapUriPath() {
        while(true) {
            try {
                System.out.print("Choose path: \n");
                String uriPath = reader.readLine();
                if(uriPath != null) {
                    return uriPath;
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }
    void sendMessage(CoapMessage coapMessage) {
        CoapMesssageParser parser = new CoapMesssageParser();
        try (var socket = new Socket(hostname, port)) {
            byte [] packet = parser.createCoapMessage(coapMessage);
            ByteUtil.printBytesAsString(packet);
            socket.getOutputStream().write(packet);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
