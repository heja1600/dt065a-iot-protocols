package src.app;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ItemEvent;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import src.config.ServerConfig;
import src.listener.UniformCallback;
import src.model.coap.CoapCode;
import src.model.coap.CoapMessage;
import src.model.coap.CoapType;
import src.model.coap.option.AbstractCoapOption;
import src.model.coap.option.CoapOptionNumberEnum;
import src.parser.CoapMessageParser;
import src.server.CoapServer.ServerType;
import src.util.ByteUtil;
import src.util.CoapUtil;
import src.util.MessageSenderUtil;
import src.util.SwingUtil;

public class CoapClientApplication extends JFrame {
    CoapMessage message;
    private static final long serialVersionUID = 1L;
    String hostname;
    ServerType serverType;
    JTextArea coapResponseBits;
    JTextArea coapResponse;
    JTextArea coapRequest;
    JTextArea coapRequestBits;
    Integer port;
    JPanel gridpPanel;
    Map<Integer, AbstractCoapOption<?>> options = new HashMap<>();
    JTextField messageIdComponent;
    public static void main(String[] args) throws Exception {
        CoapClientApplication program = new CoapClientApplication("coap.me", ServerConfig.COAP_SERVER_PORT, ServerType.UDP);
        program.createContent();
        program.setVisible(true);
    }

    public CoapClientApplication(String hostname, Integer port, ServerType serverType) throws Exception {
        super("Coap Client");
        this.hostname = hostname;
        this.serverType = serverType;
        this.port = port;
        this.message = new CoapMessage().setType(CoapType.NON).setVersion(1);
    }   

    private void updateMessageId(){
        Random r = new Random();
        int low = 0;
        int high = 65535;
        int result = r.nextInt(high-low) + low;
        messageIdComponent.setText("" + result);

    }

    private void createContent() {
        this.setSize(900, 550);
        this.setLocation(100, 100);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(true);


        Container mainContainer = this.getContentPane();
        mainContainer.setLayout(new BorderLayout(8, 6));

        /** TOP PANEL START */
        JPanel topPanel = new JPanel();

        topPanel.setLayout(new FlowLayout(5));
        JButton sendButton = new JButton("Send");
        sendButton.addActionListener(event -> {
            sendMessage(message, null);
        });
        topPanel.add(sendButton);
        mainContainer.add(topPanel, BorderLayout.NORTH);
        /** TOP PANEL END */

        /** MIDDLE PANEL START */
        JPanel middlePanel = new JPanel();
        middlePanel.setLayout(new FlowLayout(4, 4, 4));

        /** Grid PANEL START */
        gridpPanel = new JPanel();
        gridpPanel.setLayout(new BoxLayout(gridpPanel, BoxLayout.Y_AXIS));

        // CoapCode
        JLabel coapCodeLabel = new JLabel("Code:");
        JComboBox<CoapCode> coapCodeComponent = new JComboBox<CoapCode>(CoapCode.values());
        coapCodeComponent.setSelectedItem(0);
        coapCodeComponent.addItemListener(event -> {
            message.setCode((CoapCode) coapCodeComponent.getSelectedItem());
        });

        // Type
        JLabel coapTypeLabel = new JLabel("Code:");
        JComboBox<CoapType> coapTypeComponent = new JComboBox<CoapType>(CoapType.values());
        coapTypeComponent.setSelectedItem(1);
        coapTypeComponent.addItemListener(event -> {
            message.setType((CoapType) coapTypeComponent.getSelectedItem());
        });

        // ServerType
        JLabel serverTypeLabel = new JLabel("Server Type:");
        JComboBox<ServerType> serverTypeComponent = new JComboBox<ServerType>(ServerType.values());
        serverTypeComponent.setSelectedItem(0);
        serverTypeComponent.addItemListener(event -> {
            serverType = (ServerType) serverTypeComponent.getSelectedItem();
        });
        /** Payload */
        JLabel payloadLabel = new JLabel("Payload:");
        JTextField payloadComponent = new JTextField(20);
        SwingUtil.addChangeListener(payloadComponent, message, (message, value) -> {
            message.setPayload(value == "" ? null : value);
        });

        /** Token */
        JLabel tokenLabel = new JLabel("token:");
        JTextField tokenComponent = SwingUtil.createTextField("Token: ", null, 8);
        SwingUtil.addChangeListener(tokenComponent, message, (message, value) -> {
            try {
                message.setToken(value == "" ? null : value);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        });

        /** hostname */
        JLabel hostnameLabel = new JLabel("hostname:");
        JTextField hostnameComponent = SwingUtil.createTextField("hostname: ", hostname);
        SwingUtil.addChangeListener(hostnameComponent, message, (message, value) -> this.hostname = value);

        /** Message ID */
        JLabel messageIdLabel = new JLabel("Message Id:");
        messageIdComponent = SwingUtil.createNumberInput("Message Id", 0);
        messageIdComponent.setEditable(false);
        updateMessageId();
        // CoapCode
        JLabel coapOptionLabel = new JLabel("Add Coap Option:");
        JComboBox<CoapOptionNumberEnum> coapOptionComponent = new JComboBox<CoapOptionNumberEnum>(
                CoapOptionNumberEnum.values());
        coapOptionComponent.setSelectedItem(null);
        coapOptionComponent.addItemListener(e -> {
            if (ItemEvent.SELECTED == e.getStateChange()) {
                addOption((CoapOptionNumberEnum) coapOptionComponent.getSelectedItem());
                coapOptionComponent.setSelectedItem(null);
            }
        });

        SwingUtil.addChangeListener(messageIdComponent, message, (message, value) -> {
            try {
                if (value.isBlank()) {
                    message.setMessageId(0);
                } else {
                    message.setMessageId(Integer.parseInt(value));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        gridpPanel.add(SwingUtil.group(
                new Component[] { payloadLabel, coapCodeLabel, messageIdLabel, tokenLabel, hostnameLabel, coapTypeLabel,
                        serverTypeLabel, coapOptionLabel },
                new Component[] { payloadComponent, coapCodeComponent, messageIdComponent, tokenComponent,
                        hostnameComponent, coapTypeComponent, serverTypeComponent, coapOptionComponent }));

        JPanel coapRequestBitsPanel = new JPanel();
        coapRequestBits = new JTextArea(10, 20);
        coapRequestBits.setEditable(false);
        coapRequestBitsPanel.add(new JScrollPane(coapRequestBits, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS));

        JPanel coapResponseBitsPanel = new JPanel();
        coapResponseBits = new JTextArea(10, 20);
        coapResponseBits.setEditable(false);
        coapResponseBitsPanel.add(new JScrollPane(coapResponseBits, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS));

        JPanel coapResponsePanel = new JPanel();
        coapResponse = new JTextArea(10, 20);
        coapResponse.setEditable(false);
        coapResponsePanel.add(new JScrollPane(coapResponse, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS));

        JPanel coapRequestPanel = new JPanel();
        coapRequest = new JTextArea(10, 20);
        coapRequest.setEditable(false);
        coapRequestPanel.add(new JScrollPane(coapRequest, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS));

        JPanel leftVerticalPanel = new JPanel();
        leftVerticalPanel.setLayout(new BoxLayout(leftVerticalPanel, BoxLayout.Y_AXIS));
        leftVerticalPanel.add(new JLabel("Coap Request Bits"));
        leftVerticalPanel.add(coapRequestBitsPanel);
        leftVerticalPanel.add(new JLabel("Coap Response Bits"));
        leftVerticalPanel.add(coapResponseBitsPanel);

        JPanel rightVerticalPanel = new JPanel();
        rightVerticalPanel.setLayout(new BoxLayout(rightVerticalPanel, BoxLayout.Y_AXIS));
        rightVerticalPanel.add(new JLabel("Coap request"));
        rightVerticalPanel.add(coapRequestPanel);
        rightVerticalPanel.add(new JLabel("Coap Response"));
        rightVerticalPanel.add(coapResponsePanel);

        middlePanel.add(gridpPanel);
        middlePanel.add(rightVerticalPanel);
        middlePanel.add(leftVerticalPanel);
        mainContainer.add(middlePanel, BorderLayout.WEST);
    }


    public void sendMessage(CoapMessage coapMessage, UniformCallback<CoapMessage> callback) {

        CoapMessageParser parser = new CoapMessageParser();
        src.model.Container<CoapMessage> response = new src.model.Container<>();
        byte[] data = parser.encode(coapMessage);
        try {
            if (this.serverType == ServerType.UDP) {
                MessageSenderUtil.udpSendAndRecieve(data, port, hostname, 1024, message -> {
                    CoapMessage coapResponse = parser.decode(message);
                    if(callback != null) {
                        callback.call(coapResponse);
                    }
         
                });
            } else {
                MessageSenderUtil.tcpSendAndReceive(data, port, hostname, message -> {
                    CoapMessage coapResponse = parser.decode(message);
                    if(callback != null) {
                        callback.call(coapResponse);
                    }
                });
        
            }
     
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
    public void sendMessage() {
        this.message.resetOptions();
        options.forEach((key, object) -> {
            try {
                this.message.addOption(object);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }); 
        CoapMessageParser parser = new CoapMessageParser();
        sendMessage(this.message, response -> {
            coapRequest.setText(this.message.toString());
            coapRequestBits.setText(ByteUtil.bytesToString(parser.encode(response)));
            coapResponseBits.setText(null);
            coapResponse.setText(null);
            coapResponse.setText(response.toString());
            updateMessageId();
        });
        
    }

    public void addOption(CoapOptionNumberEnum optionNumber) {
        Integer randomNumber = -1;
        while(randomNumber == -1 || options.containsKey(randomNumber)) 
            randomNumber = new Random().nextInt();
        final Integer finalRandomNumber = randomNumber;
        try {
            JPanel parent = gridpPanel;
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
            JButton removeButton = new JButton("Remove");
            JLabel jLabel = new JLabel("" + optionNumber);
            JTextField input = new JTextField();

            removeButton.addActionListener(event -> {
                parent.remove(panel);
                options.remove(finalRandomNumber);
                parent.revalidate();
            });


            SwingUtil.addChangeListener(input, message, (object, value) -> {

                try {
      
                    options.put(finalRandomNumber, CoapUtil.resolveOption(optionNumber, value.getBytes()));
       
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            });

            panel.add(jLabel);
            panel.add(input);
            panel.add(removeButton);

            parent.add(panel);
            parent.revalidate();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}