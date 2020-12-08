package client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramSocket;
import java.util.HashMap;
import java.util.Map;

import server.Server.ServerType;
import shared.config.ServerConfig;
import shared.model.coap.CoapCode;
import shared.model.coap.CoapMessage;
import shared.model.coap.CoapType;
import shared.model.coap.option.AbstractCoapOption;
import shared.model.coap.option.CoapOptionNumberEnum;
import shared.model.coap.option.CoapOptionUriPath;
import shared.service.CoapMesssageParser;
import shared.service.CoapOptionResolver;
import shared.util.ByteUtil;
import shared.util.MessageSenderUtil;

/** Very simple program to test the Coap Server */
public class Program {

    public enum ProgramOption {
        SEND(0), PREVIEW(1), PAYLOAD(2), TOKEN(3), VERSION(4), CODE(5), OPTIONS(6), HOSTNAME(7), PORT(8), EXIT(9),
        TYPE(10), SERVER_TYPE(11);

        Integer value;

        ProgramOption(Integer value) {
            this.value = value;
        }

        public Integer get() {
            return this.value;
        }

        // Lookup table
        private static final Map<Integer, ProgramOption> lookup = new HashMap<>();

        // Populate the lookup table on loading time
        static {
            for (ProgramOption env : ProgramOption.values()) {
                lookup.put(env.get(), env);
            }
        }

        // This method can be used for reverse lookup purpose
        public static ProgramOption get(Integer value) {
            return lookup.get(value);
        }
    }
    ServerType serverType = ServerType.UDP;
    CoapMessage coapMessage;
    boolean runProgram = true;
    String hostname = "localhost";
    int port = ServerConfig.SERVER_PORT;
    BufferedReader reader;

    public static void main(String[] args) throws Exception {
        new Program();
    }

    Program() throws Exception {
        reader = new BufferedReader(new InputStreamReader(System.in));
        startProgram();
    }

    void startProgram() throws Exception {
        // coapMessage = new CoapMessage()
        // .setMessageId(32052)
        // .addOption(new CoapOptionUriPath("temperature"))
        // .setCode(CoapCode.GET)
        // .setVersion(1)
        // .setType(CoapType.CON);

        coapMessage = new CoapMessage()
                .setVersion(1)
                .setType(CoapType.NON)
                .setCode(CoapCode.GET)
                .setMessageId(0xaa55)
                .addOption(new CoapOptionUriPath("test"));

        while (runProgram) {
            clearScreen();
            ProgramOption option = getProgramOption();
            switch (option) {
                case SEND: {
                    sendMessage(coapMessage);
                    break;
                }
                case PREVIEW: {
                    clearScreen();
                    CoapMesssageParser.printCoapMessage(coapMessage);
                    pressKeyContinue();
                    break;
                }
                case PAYLOAD: {
                    setPayload();
                    break;
                }
                case HOSTNAME: {
                    setHostname();
                    break;
                }
                case PORT: {
                    setPort();
                    break;
                }
                case TOKEN: {
                    setToken();
                    break;
                }
                case VERSION: {
                    setVersion();
                    break;
                }
                case CODE: {
                    setCode();
                    break;
                }
                case TYPE: {
                    setType();
                    break;
                }
                case OPTIONS: {
                    CoapOptionNumberEnum optionNumber = getOptionNumber();
                    setOptionValue(optionNumber);
                    break;
                }
                case SERVER_TYPE: {
                    setServerType();
                    break;
                }
                case EXIT: {
                    runProgram = false;
                }
            }
        }
    }

    void setServerType() {
        while (true) {
            try {
                ServerType[] options = ServerType.values();
                System.out.println("Välj ett alternativ:");
                for (int i = 0; i < options.length; i++) {
                    System.out.println(i + "." + options[i]);
                }
                this.serverType = options[Integer.parseInt(reader.readLine())];
                break;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    ProgramOption getProgramOption() {
        while (true) {
            try {
                ProgramOption[] options = ProgramOption.values();
                System.out.println("Välj ett alternativ:");
                for (int i = 0; i < options.length; i++) {
                    System.out.println(i + "." + options[i]);
                }
                return options[Integer.parseInt(reader.readLine())];

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    CoapOptionNumberEnum getOptionNumber() {
        while (true) {
            try {
                clearScreen();
                CoapOptionNumberEnum[] options = CoapOptionNumberEnum.values();
                System.out.println("Välj ett alternativ:");
                for (int i = 0; i < options.length; i++) {
                    System.out.println(i + "." + options[i]
                            + (CoapOptionResolver.isAvailable(options[i]) ? "" : " - NOT IMPLEMENTED"));
                }
                int index = Integer.parseInt(reader.readLine());
                if (CoapOptionResolver.isAvailable(options[index])) {
                    return options[index];
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    void setOptionValue(CoapOptionNumberEnum optionNumber) {
        while (true) {
            try {

                System.out.println("Välje värde för option: " + optionNumber);

                String optionValue = reader.readLine();
                byte[] optionValueInBytes;
                if (CoapOptionResolver.resolveType(optionNumber) == Integer.class) {
                    optionValueInBytes = ByteUtil.integerToByteArray(Integer.parseInt(optionValue));
                } else {
                    optionValueInBytes = ByteUtil.stringToByteArray(optionValue);
                }
                AbstractCoapOption<?> coapOption = CoapOptionResolver.resolveOption(optionNumber, optionValueInBytes);

                if (!coapOption.isRepetable() && coapMessage.getOptions().containsKey(optionNumber)
                        && !coapMessage.getOptions().get(optionNumber).isEmpty()) {
                    System.out.println("The value is already registered and can only be registered once");
                    System.out.println("1. Remove previous value");
                    System.out.println("2. Ovveride value");
                    System.out.println("3. Avbryt");
                    int choice = Integer.parseInt(reader.readLine());

                    if (choice == 1) {
                        coapMessage.getOptions().put(optionNumber, null);
                    } else if (choice == 2) {
                        coapMessage.getOptions().put(optionNumber, null);
                        coapMessage.addOption(coapOption);
                    }
                } else {
                    coapMessage.addOption(coapOption);
                }

                break;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    void setType() {
        while (true) {
            try {
                clearScreen();
                CoapType[] types = CoapType.values();
                System.out.println("Välj ett alternativ, tidigare värde: " + coapMessage.getCode());
                for (int i = 0; i < types.length; i++) {
                    System.out.println(i + "." + types[i]);
                }
                coapMessage.setType(CoapType.get(Integer.parseInt(reader.readLine())));
                break;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    void setVersion() {
        while (true) {
            clearScreen();
            try {
                System.out.println("Sätt version 0-3, tidigare värde: " + coapMessage.getVersion());
                coapMessage.setVersion(Integer.parseInt(reader.readLine()));
                break;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    void setHostname() {
        while (true) {
            clearScreen();
            try {
                System.out.println("Skriv ett hostname, tidigare värde: " + hostname);
                hostname = reader.readLine();
                break;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    void setPort() {
        while (true) {
            clearScreen();
            try {
                System.out.println("Skriv ett port, tidigare värde: " + port);
                port = Integer.parseInt(reader.readLine());
                break;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    void setToken() {
        while (true) {
            clearScreen();
            try {
                System.out.println("Ange token för meddelande, tidagare värde: " + coapMessage.getToken());
                coapMessage.setToken(reader.readLine());
                break;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    void setPayload() {
        while (true) {
            clearScreen();
            try {
                System.out.println("Ange payload för meddelande, tidagare värde: " + coapMessage.getPayload());
                coapMessage.setPayload(reader.readLine());
                break;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    void setCode() {
        while (true) {
            clearScreen();
            try {
                CoapCode[] codes = CoapCode.values();
                System.out.println("Välj ett alternativ, tidigare värde: " + coapMessage.getCode());
                for (int i = 1; i < codes.length + 1; i++) {
                    System.out.println(i + "." + codes[i - 1]);
                }

                coapMessage.setCode(CoapCode.get(Integer.parseInt(reader.readLine())));
                break;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
 
    void sendMessage(CoapMessage coapMessage) {
        clearScreen();


        CoapMesssageParser parser = new CoapMesssageParser();
        byte[] data = parser.createCoapMessage(coapMessage);
        CoapMesssageParser.printCoapMessage(coapMessage);
        try (var socket = new DatagramSocket()) {
            if(this.serverType == ServerType.UDP) {
                MessageSenderUtil.udpSendMessage(data, port, hostname);
            } else {
                MessageSenderUtil.tcpSendMessage(data, port, hostname);
            }

        } catch(Exception e) {
            e.printStackTrace();
        }


        pressKeyContinue(); 
    }

    void pressKeyContinue() {
        System.out.println("Press Enter key to continue...");
        try
        {
            reader.readLine();
        }  
        catch(Exception e)
        {}  
    }

    void clearScreen() {
        System.out.print("\033[H\033[2J");  
        System.out.flush();  
    }
}
