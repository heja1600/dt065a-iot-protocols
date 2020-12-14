package shared.model.mqtt;

import shared.util.ByteUtil;

public class MqttConnectFlag {
    boolean UserNameFlag = false;
    boolean PasswordFlag = false;
    boolean WillRetainFlag = false;
    MqttQoS mqttQoS = MqttQoS.AT_MOST_ONCE;
    boolean willFlagFlag = false;
    boolean cleanSessionFlag = false;
    boolean reserved = false;

    public int get() {
        return ByteUtil.boolArrayToInteger(
            UserNameFlag, 
            PasswordFlag, 
            willFlagFlag, 
            (mqttQoS.get() & 2) == 2,
            (mqttQoS.get() & 1) == 1,
            willFlagFlag,
            cleanSessionFlag,
            reserved
        );
    }

	public boolean isUserNameFlag() {
		return UserNameFlag;
	}

	public MqttConnectFlag setUserNameFlag() {
		UserNameFlag = true;
        return this;
    }

	public boolean isPasswordFlag() {
		return PasswordFlag;
	}

	public MqttConnectFlag setPasswordFlag() {
		PasswordFlag = true;
        return this;
    }

	public boolean isWillRetainFlag() {
		return WillRetainFlag;
	}

	public MqttConnectFlag setWillRetainFlag() {
		WillRetainFlag = true;
        return this;
    }

	public MqttQoS getMqttQoS() {
		return mqttQoS;
	}

	public MqttConnectFlag setMqttQoS(MqttQoS mqttQoS) {
		this.mqttQoS = mqttQoS;
        return this;
    }

	public boolean isWillFlagFlag() {
		return willFlagFlag;
	}

	public MqttConnectFlag setWillFlagFlag() {
		this.willFlagFlag = true;
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
}
