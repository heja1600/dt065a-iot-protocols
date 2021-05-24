package src.model.mqtt;

import src.util.ByteUtil;

public class MqttConnectFlag {
	boolean userNameFlag = false;
	boolean passwordFlag = false;
	boolean willRetainFlag = false;
	MqttQoS mqttQoS = MqttQoS.AT_MOST_ONCE;
	boolean willFlag = false;
	/**
	 * If CleanSession is set to 0, the Server MUST resume communications with the
	 * Client based on state from the current Session (as identified by the Client
	 * identifier). If there is no Session associated with the Client identifier the
	 * Server MUST create a new Session. The Client and Server MUST store the
	 * Session after the Client and Server are disconnected. After the disconnection
	 * of a Session that had CleanSession set to 0, the Server MUST store further
	 * QoS 1 and QoS 2 messages that match any subscriptions that the client had at
	 * the time of disconnection as part of the Session state. It MAY also store QoS
	 * 0 messages that meet the same criteria.
	 * 
	 * If CleanSession is set to 1, the Client and Server MUST discard any previous
	 * Session and start a new one. This Session lasts as long as the Network
	 * Connection. State data associated with this Session MUST NOT be reused in any
	 * subsequent Session
	 */
	boolean cleanSessionFlag = false;
	boolean reserved = false; // The Server MUST validate that the reserved flag in the CONNECT Control Packet
								// is set to zero and disconnect the Client if it is not zero

	public int get() {
		return ByteUtil.boolArrayToInteger(
            userNameFlag, 
            passwordFlag, 
            willRetainFlag, 
            (mqttQoS.get() & 2) == 2,
            (mqttQoS.get() & 1) == 1, 
            willFlag, 
            cleanSessionFlag, 
            reserved
        );
	}

    public MqttConnectFlag set(int bit) {
        this.userNameFlag = ((bit & 0b10000000) >> 7) == 1;
        this.passwordFlag = ((bit & 0b01000000) >> 6) == 1;
        this.willRetainFlag = ((bit & 0b00100000) >> 5) == 1;
        this.mqttQoS = MqttQoS.get(bit & 0b00011000);
        this.willFlag = ((bit & 0b00000100) >> 2) == 1;
        this.cleanSessionFlag = ((bit & 0b00000010) >> 1) == 1;
        return this;
    }
	public boolean isUserNameFlag() {
		return userNameFlag;
	}

	public MqttConnectFlag setUserNameFlag() {
		userNameFlag = true;
		return this;
	}

	public boolean isPasswordFlag() {
		return passwordFlag;
	}

	public MqttConnectFlag setPasswordFlag() {
		passwordFlag = true;
		return this;
	}

	public boolean isWillRetainFlag() {
		return willRetainFlag;
	}

	public MqttConnectFlag setWillRetainFlag() {
		willRetainFlag = true;
		return this;
	}

	public MqttQoS getMqttQoS() {
		return mqttQoS;
	}

	public MqttConnectFlag setMqttQoS(MqttQoS mqttQoS) {
		this.mqttQoS = mqttQoS;
		return this;
	}

	public boolean isWillFlag() {
		return willFlag;
	}

	public MqttConnectFlag setWillFlag() {
		this.willFlag = true;
		return this;
	}

	public boolean isCleanSessionFlag() {
		return cleanSessionFlag;
	}

	public MqttConnectFlag setCleanSessionFlag() {
		this.cleanSessionFlag = true;
		return this;
	}

	public boolean isReserved() {
		return reserved;
	}

	public MqttConnectFlag setReserved() {
		this.reserved = true;
		return this;
    }
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("- Mqtt Connect Flag" + "\n");
        builder.append("Username-flag: " + isUserNameFlag() + "\n");
        builder.append("password-flag: " + isPasswordFlag() + "\n");
        builder.append("willRetain-flag: " + isWillRetainFlag() + "\n");
        builder.append("Qos: " + getMqttQoS() + "\n");
        builder.append("cleanSession-flag: " + isCleanSessionFlag() + "\n");
        builder.append("reserved: " + isReserved() + "\n");
        System.out.print("\n");
        return builder.toString();
    }
}
