package model.mqtt.packet;

import java.util.HashMap;
import java.util.Map;

import model.mqtt.MqttControlPacketType;

public class MqttConnAckControlPacket extends AbstractMqttControlPacket {

    private MqttConnectReturnCode mqttConnectReturnCode;

    /**
     * If the Server accepts a connection with CleanSession set to 1, 
     * the Server MUST set Session Present to 0 in the CONNACK packet 
     * in addition to setting a zero return code in the CONNACK packet.
     * 
     * If the Server accepts a connection with CleanSession set to 0, 
     * the value set in Session Present depends on whether the Server 
     * already has stored Session state for the supplied client ID. 
     * If the Server has stored Session state, it MUST set Session Present 
     * to 1 in the CONNACK packet. If the Server does not have stored 
     * Session state, it MUST set Session Present to 0 in the CONNACK packet. 
     * This is in addition to setting a zero return code in the CONNACK packet
     */
    private boolean sessionPresentFlag;

	public MqttConnAckControlPacket() {
        super(MqttControlPacketType.CONNACK);
        mqttConnectReturnCode = MqttConnectReturnCode.CONNECTION_ACCEPTED;
        sessionPresentFlag = false;
    }

    public MqttConnectReturnCode getMqttConnectReturnCode() {
        return mqttConnectReturnCode;
    }

    public MqttConnAckControlPacket setMqttConnectReturnCode(MqttConnectReturnCode mqttConnectReturnCode) {
        this.mqttConnectReturnCode = mqttConnectReturnCode;
        return this;
    }

    public boolean isSessionPresentFlag() {
        return sessionPresentFlag;
    }

    public MqttConnAckControlPacket setSessionPresentFlag(boolean sessionPresentFlag) {
        this.sessionPresentFlag = sessionPresentFlag;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("- Mqtt CONNACK Control Packet" + "\n");
        builder.append("Connect Return Code: " + getMqttConnectReturnCode() + "\n");
        builder.append("Session Present flag: " + isSessionPresentFlag() + "\n");
        return builder.toString();
    }
    public enum MqttConnectReturnCode {

        /**
         * 0x00 Connection Accepted.
         * Connection accepted
         */
        CONNECTION_ACCEPTED(0),
        /**
         * 0x01 Connection Refused, unacceptable protocol version.
         * The Server does not support the level of 
         * the MQTT protocol requested by the Client
         */
        UNACCEPTABLE_PRTOTOCOL_VERSION(1),
        /**
         * 0x02 Connection Refused, identifier rejected.
         * The Client identifier is correct UTF-8 but 
         * not allowed by the Server
         */
        IDENTIFIER_REJECTED(2),
        /**
         * 0x03 Connection Refused, Server unavailable.
         * The Network Connection has been made but the MQTT 
         * service is unavailable
         */
        SERVER_UNAVAILABLE(3),
        /**
         * 0x04 Connection Refused, bad user name or password.
         * The data in the user name or password is malformed
         */
        BAD_USERNAME_OR_PASSWORD(4),
        /**
         * 0x05 Connection Refused, not authorized.
         * The Client is not authorized to connect
         */
        NOT_AUTHORIZED(5);
        /**
         * 0x06 <-> 0x0f
         * Reserved for future use
         */

      
    
        private int mqttConnectReturnCode;
    
        private MqttConnectReturnCode(int mqttConnectReturnCode)  {
            this.mqttConnectReturnCode = mqttConnectReturnCode;
        }
    
        
        public int get() {
            return this.mqttConnectReturnCode;
        }
    
        //Lookup table
        private static final Map<Integer, MqttConnectReturnCode> lookup = new HashMap<>();
    
        //Populate the lookup table on loading time
        static
        {
            for(MqttConnectReturnCode env : MqttConnectReturnCode.values())
            {
                lookup.put(env.get(), env);
            }
        }
        
        //This method can be used for reverse lookup purpose
        public static MqttConnectReturnCode get(int mqttConnectReturnCode) 
        {
            return lookup.get( mqttConnectReturnCode);
        }
    }
}
