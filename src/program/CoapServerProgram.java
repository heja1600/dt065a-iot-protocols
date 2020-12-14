package program;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import program.swing.SwingUtil;
import server.CoapServer;
import server.CoapServer.ServerType;
import server.listener.ExtendedServerListener;
import server.service.TCPMessageReceiver;
import server.service.UDPMessageReceiver;
import shared.model.coap.CoapMessage;
import shared.service.CoapMessageParser;
import shared.util.ByteUtil;

public class CoapServerProgram extends JFrame implements ExtendedServerListener<CoapMessage> {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    CoapServer<?> server;
    ServerType serverType = ServerType.UDP;
    JTextArea coapResponseBits;
    JTextArea coapResponse;
    JTextArea coapRequest;
    JTextArea coapRequestBits;
    JButton startServerButton = new JButton("Start Server");

    public static void main(String[] args) throws Exception {
        CoapServerProgram program = new CoapServerProgram();
        program.setVisible(true);
    }

    public CoapServerProgram() throws Exception {
        super("Coap Server");
        this.setSize(900, 550);
        this.setLocation(100, 100);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        startServerButton.setEnabled(false);
        createContent();
    }   

    private void createContent() {
        Container mainContainer = this.getContentPane();
        mainContainer.setLayout(new BorderLayout(8, 6));

        /** TOP PANEL START */
        JPanel topPanel = new JPanel();

        topPanel.setLayout(new FlowLayout(5));
        startServerButton.addActionListener(event -> {
            if(server != null) {
                if(server.isRunning()) {
                    stopServer();
                } else {
                    startServer();
                }
        
            } else {
                startServer();
            }
        });
        topPanel.add(startServerButton);

   
        mainContainer.add(topPanel, BorderLayout.NORTH);

        /** MIDDLE PANEL START */
        JPanel middlePanel = new JPanel();
        middlePanel.setLayout(new FlowLayout(4, 4, 4));

        /** Grid PANEL START */
        JPanel gridpPanel = new JPanel();
        gridpPanel.setLayout(new BoxLayout(gridpPanel, BoxLayout.Y_AXIS));
  

        // ServerType
        JLabel serverTypeLabel = new JLabel("Server Type:");
        JComboBox<ServerType> serverTypeComponent = new JComboBox<ServerType>(ServerType.values());
        serverTypeComponent.setSelectedItem(null);
        serverTypeComponent.addItemListener(event -> {
            serverType = (ServerType) serverTypeComponent.getSelectedItem();
            startServerButton.setText("Start server");
            startServerButton.setEnabled(true);
        });
        

        gridpPanel.add(SwingUtil.group(
            new Component[] { serverTypeLabel},
            new Component[] { serverTypeComponent}));


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
        rightVerticalPanel.add(new JLabel("Coap Request     "));
        rightVerticalPanel.add(coapRequestPanel);
        rightVerticalPanel.add(new JLabel("Coap Response"));
        rightVerticalPanel.add(coapResponsePanel);


        middlePanel.add(gridpPanel);
        middlePanel.add(rightVerticalPanel);
        middlePanel.add(leftVerticalPanel);
        mainContainer.add(middlePanel, BorderLayout.WEST);
    }

    void startServer() {

        switch(serverType) {
            case UDP: {
                this.server = new CoapServer<>(new UDPMessageReceiver<CoapMessage>(new CoapMessageParser()));
            }
            case TCP: {
                this.server = new CoapServer<>(new TCPMessageReceiver<CoapMessage>(new CoapMessageParser()));
            }    
        }

        server.setListener(this);
        server.startServer();

        startServerButton.setText("Stop server");
    }

    void stopServer() {
        if(server != null) {
            server.stopServer();
            server = null;
        }
        startServerButton.setText("Start server");
    }
    @Override
    public void onMessageReceived(CoapMessage message) {
        CoapMessageParser parser = new CoapMessageParser();
        coapRequestBits.setText(ByteUtil.bytesToString(parser.encode(message)));
        coapRequest.setText(message.toString());
    }

    @Override
    public void onMessageSent(CoapMessage message) {
        CoapMessageParser parser = new CoapMessageParser();
        coapResponseBits.setText(ByteUtil.bytesToString(parser.encode(message)));
        coapResponse.setText(message.toString());
    }
}
