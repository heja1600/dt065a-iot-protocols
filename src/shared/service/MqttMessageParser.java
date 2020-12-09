package shared.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

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

            buffer = byteArrayOutputStream.toByteArray();
        } catch(Exception e) {
            e.printStackTrace();
        }
		return buffer;
	}   
}
