package model.mqtt.packet;

import model.mqtt.MqttConnectFlag;
import model.mqtt.MqttControlPacketType;
import model.mqtt.MqttQoS;
/**
 * A Client can only send the CONNECT Packet once over a Network Connection. 
 * The Server MUST process a second CONNECT Packet sent from a Client as a 
 * protocol violation and disconnect the Client 
 */
public class MqttConnectControlPacket extends AbstractMqttControlPacket {
    private MqttConnectFlag connectFlag;
    private String protocolName;
    private Integer protocolLevel;
    private Integer keepAlive;



    /** Payload 
     *  Must be the following order:
     */ 
    private String clientIdentifier;
    private String willTopic;
    private String willMessage;
    private String username;
    private String password; 
    
    public MqttConnectControlPacket() {
        super(MqttControlPacketType.CONNECT);
        protocolName = "MQTT";
        protocolLevel = 4; // 3.1.1
        keepAlive = 60;
        connectFlag = new MqttConnectFlag();
    }

    public MqttConnectFlag getConnectFlag() {
        if(this.connectFlag == null) {
            this.connectFlag = new MqttConnectFlag()
                .setUserNameFlag()
                .setPasswordFlag()
                .setWillRetainFlag()
                .setMqttQoS(MqttQoS.EXACLY_ONCE)
                .setWillFlag()
                .setCleanSessionFlag();
        }
        return this.connectFlag;
    }

    public MqttConnectControlPacket setConnectFlag(MqttConnectFlag connectFlag) {
        this.connectFlag = connectFlag;
        return this;
    }

	public String getProtocolName() {
		return protocolName;
	}

	public MqttConnectControlPacket setProtocolName(String protocolName) {
        this.protocolName = protocolName;
        return this;
	}

	public Integer getProtocolLevel() {
		return protocolLevel;
	}

	public MqttConnectControlPacket setProtocolLevel(Integer protocolLevel) throws Exception {
        if(protocolLevel < 0x0 || protocolLevel > 0xff) {
            throw new Exception("The value of protocol level should only be one byte");
        }
        this.protocolLevel = protocolLevel;
        return this;
    }
    
    public Integer getKeepAlive() {
        return this.keepAlive;
    }

    public void setKeepAlive(Integer keepAlive) throws Exception {
        if(keepAlive < 0x0 || keepAlive > 0xffff) {
            throw new Exception("The value of keep alive should only be between 0-2 bytes");
        }
        this.keepAlive = keepAlive;
    }

	public String getClientIdentifier() {
		return clientIdentifier;
	}

	public MqttConnectControlPacket setClientIdentifier(String clientIdentifier) {
		this.clientIdentifier = clientIdentifier;
        return this;
    }

	public String getWillTopic() {
		return willTopic;
	}

	public MqttConnectControlPacket setWillTopic(String willTopic) {
		this.willTopic = willTopic;
        return this;
    }

	public String getWillMessage() {
		return willMessage;
	}

	public MqttConnectControlPacket setWillMessage(String willMessage) {
		this.willMessage = willMessage;
        return this;
    }

	public String getUsername() {
		return username;
	}

	public MqttConnectControlPacket setUsername(String username) {
		this.username = username;
        return this;
    }

	public String getPassword() {
		return password;
	}

	public MqttConnectControlPacket setPassword(String password) {
		this.password = password;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("- Mqtt Connect Control Packet" + "\n");
        builder.append("ProtocolName: " + getProtocolName() + "\n");
        builder.append("ProtocolLevel: " + getProtocolLevel() + "\n");
        builder.append("KeepAlive: " + getKeepAlive() + "\n");
        builder.append("ClientIdentifier: " + getClientIdentifier() + "\n");
        builder.append("WillTopic: " + getWillTopic() + "\n");
        builder.append("WillMessage: " + getWillMessage() + "\n");
        builder.append("Username: " + getUsername() + "\n");
        builder.append("Password: " + getPassword() + "\n");
        builder.append(getConnectFlag());
        System.out.print("\n");
        return builder.toString();
    }
}
