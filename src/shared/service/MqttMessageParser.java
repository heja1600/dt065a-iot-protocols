package shared.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import shared.model.mqtt.MqttConnectFlag;
import shared.model.mqtt.MqttMessage;
import shared.model.mqtt.MqttMessageType;
import shared.model.mqtt.MqttQoS;

public class MqttMessageParser implements MessageParser<MqttMessage>{

	@Override
	public MqttMessage decode(byte[] buffer) {
        MqttMessage message = new MqttMessage();
        try(ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(buffer)){

            int firstByte = byteArrayInputStream.read();
            // Set type
            message.setMqttMessageType(MqttMessageType.get((firstByte & 0xf0) >> 4));

            // Set DUP flag
            message.setDUPFlag(((firstByte & 0x8) >> 3) == 1);

            // Set Quality of Service (QOS) level
            message.setMqttQoS(MqttQoS.get(((firstByte) & 0x6) >> 1));

            // Set Retain
            message.setRetainFlag((firstByte & 0x1) == 1);


        }
        catch(Exception e) {
            e.printStackTrace();
        }

		return message;
	}

	@Override
	public byte[] encode(MqttMessage message) {

        byte [] buffer = null;
		try(ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {

            // Set Type
            int firstByte = (message.getMqttMessageType().get() & 0xf) << 4;

            // Set DUP flag
            firstByte = (firstByte | ((message.isDUPFlag() ? 1 : 0) << 3));

            // Set Quality of Service (QOS) level
            firstByte = firstByte | (message.getMqttQoS().get() << 1);

            // Set Retain
            firstByte =  firstByte | (message.isRetainFlag() ? 1 : 0);

            byte [] extraBuffer = null;


            switch(message.getMqttMessageType()) {
                case CONNECT: {
                    extraBuffer = encodeConnectMessage(message);
                    break;
                }
                default: {
                    break;
                }
            }

            if(extraBuffer != null) {
                byteArrayOutputStream.write(extraBuffer.length);
                byteArrayOutputStream.write(extraBuffer);
            }

            buffer = byteArrayOutputStream.toByteArray();
        } catch(Exception e) {
            e.printStackTrace();
        }
		return buffer;
    }   
    
    /**
     * Encoder for connect message
     * @param message
     * @param byteArrayOutputStream
     */
    private byte [] encodeConnectMessage(MqttMessage message) {
        byte [] buffer = null;
    	try(ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            /** Connect packer variable header */
            /** Protocol name */
            String protocolName = "MQTT";
            byteArrayOutputStream.write(0); // MSB
            byteArrayOutputStream.write(protocolName.length()); // LSB
            byteArrayOutputStream.write("MQTT".getBytes());

            /** Protocol level */
            byteArrayOutputStream.write(4); // Level 4

            /** Connect flags */
            MqttConnectFlag connectFlag = new MqttConnectFlag()
                .setCleanSessionFlag();
            byteArrayOutputStream.write(connectFlag.get()); 

            /** Keep alive */
            int keepAliveSeconds = 60;
            byteArrayOutputStream.write(0); // MSB
            byteArrayOutputStream.write(keepAliveSeconds); // LSB

            /** Client ID */

            String clientId = "DIGI";
            byteArrayOutputStream.write(0);
            byteArrayOutputStream.write(clientId.length());
            byteArrayOutputStream.write(clientId.getBytes());
            
            buffer = byteArrayOutputStream.toByteArray();
        } catch(Exception e) {
            e.printStackTrace();
        }
        return buffer;
    }
}
