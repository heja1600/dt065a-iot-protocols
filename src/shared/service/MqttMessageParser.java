package shared.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import shared.model.mqtt.MqttConnectFlag;
import shared.model.mqtt.MqttControlPacketType;
import shared.model.mqtt.MqttMessage;
import shared.model.mqtt.MqttQoS;
import shared.model.mqtt.packet.AbstractMqttControlPacket;
import shared.model.mqtt.packet.MqttConnectControlPacket;
import shared.util.ByteUtil;

public class MqttMessageParser implements MessageParser<MqttMessage> {

    @Override
    public MqttMessage decode(byte[] buffer) {
        MqttMessage message = new MqttMessage();
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(buffer)) {

            int firstByte = byteArrayInputStream.read();

            // Set type
            message.setMqttControlPacketType(MqttControlPacketType.get((firstByte & 0xf0) >> 4));

            // If its a publish message, set otherwise reserved bits
            if (message.getMqttControlPacketType() == MqttControlPacketType.PUBLISH) {
                // Set DUP flag
                message.setDUPFlag(((firstByte & 0x8) >> 3) == 1);

                // Set Quality of Service (QOS) level
                message.setMqttQoS(MqttQoS.get(((firstByte) & 0x6) >> 1));

                // Set Retain
                message.setRetainFlag((firstByte & 0x1) == 1);
            }
            // set remaining length
            Integer remainingLength = byteArrayInputStream.read();

            AbstractMqttControlPacket packet;
            switch (message.getMqttControlPacketType()) {
                case CONNECT: {
                    packet = decodeConnectMessage(byteArrayInputStream, remainingLength);
                }
                default: {
                    packet = null;
                }
            }
            message.setPacket(packet);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return message;
    }

    private MqttConnectControlPacket decodeConnectMessage(ByteArrayInputStream byteArrayInputStream,
            Integer remainingLength) throws Exception {

        MqttConnectControlPacket packet = new MqttConnectControlPacket();

        /** Set protocolname from variable header */
        byte[] variableHeader = byteArrayInputStream.readNBytes(getLength(byteArrayInputStream));
        packet.setProtocolName(new String(variableHeader));

        /** Set protocol level */
        packet.setProtocolLevel(byteArrayInputStream.read());

        /** Set connect flag */
        int connectFlag = byteArrayInputStream.read();
        packet.setConnectFlag(new MqttConnectFlag().set(connectFlag));

        /** Set Keep alive */
        Integer keepAlive = (int) byteArrayInputStream.readNBytes(2)[1];
        packet.setKeepAlive(keepAlive);
            
        if(packet.getConnectFlag().isCleanSessionFlag()) {
            packet.setClientIdentifier(
                ByteUtil.byteArrayToString(byteArrayInputStream.readNBytes(getLength(byteArrayInputStream)))
            );
        }

        if(packet.getConnectFlag().isWillFlag()) {
            packet.setWillTopic(
                ByteUtil.byteArrayToString(byteArrayInputStream.readNBytes(getLength(byteArrayInputStream)))
            );
            packet.setWillMessage(
                ByteUtil.byteArrayToString(byteArrayInputStream.readNBytes(getLength(byteArrayInputStream)))
            );
        }

        if(packet.getConnectFlag().isUserNameFlag()) {
            packet.setUsername(
                ByteUtil.byteArrayToString(byteArrayInputStream.readNBytes(getLength(byteArrayInputStream)))
            );
        }

        if(packet.getConnectFlag().isPasswordFlag()) {
            packet.setPassword(
                ByteUtil.byteArrayToString(byteArrayInputStream.readNBytes(getLength(byteArrayInputStream)))
            );
        }
        
        return packet;
    }

    private int getLength(ByteArrayInputStream byteArrayInputStream) throws IOException {
        return (int) byteArrayInputStream.readNBytes(2)[1];
    }
    
	@Override
	public byte[] encode(MqttMessage message) {

        byte [] buffer = null;
		try(ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {

            // Set Type
            int firstByte = (message.getMqttControlPacketType().get() & 0xf) << 4;

            // Set DUP flag
            firstByte = (firstByte | ((message.isDUPFlag() ? 1 : 0) << 3));

            // Set Quality of Service (QOS) level
            firstByte = firstByte | (message.getMqttQoS().get() << 1);

            // Set Retain
            firstByte =  firstByte | (message.isRetainFlag() ? 1 : 0);

            byte [] extraBuffer = null;


            switch(message.getMqttControlPacketType()) {
                case CONNECT: {
                    extraBuffer = encodeConnectMessage((MqttConnectControlPacket)message.getPacket());
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
    private byte [] encodeConnectMessage(MqttConnectControlPacket packet) {
        byte [] buffer = null;
    	try(ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            /** Connect packer variable header */
            /** Protocol name */
            byteArrayOutputStream.write(0); // MSB
            byteArrayOutputStream.write(packet.getProtocolName().length()); // LSB
            byteArrayOutputStream.write(packet.getProtocolName().getBytes());

            /** Protocol level */
            byteArrayOutputStream.write(4); // Level 4

            /** Connect flags */
            MqttConnectFlag connectFlag = new MqttConnectFlag()
                    .setCleanSessionFlag();
            byteArrayOutputStream.write(connectFlag.get()); 

            /** Keep alive */
            byteArrayOutputStream.write(0); // MSB
            byteArrayOutputStream.write(packet.getKeepAlive()); // LSB

            /** Client ID */
            byteArrayOutputStream.write(0);
            byteArrayOutputStream.write(packet.getClientIdentifier().length());
            byteArrayOutputStream.write(packet.getClientIdentifier().getBytes());
            
            buffer = byteArrayOutputStream.toByteArray();
        } catch(Exception e) {
            e.printStackTrace();
        }
        return buffer;
    }


    
}
