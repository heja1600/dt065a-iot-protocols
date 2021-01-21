package parser;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import model.mqtt.MqttConnectFlag;
import model.mqtt.MqttControlPacketType;
import model.mqtt.MqttMessage;
import model.mqtt.MqttQoS;
import model.mqtt.packet.AbstractMqttControlPacket;
import model.mqtt.packet.MqttConnAckControlPacket;
import model.mqtt.packet.MqttConnectControlPacket;
import util.ByteUtil;

public class MqttMessageParser implements MessageParser<MqttMessage> {

    @Override
    public MqttMessage decode(byte[] buffer) {
        MqttMessage message = new MqttMessage();
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(buffer)) {

            int firstByte = byteArrayInputStream.read();

            // Get type
            MqttControlPacketType mqttControlPacketType = MqttControlPacketType.get((firstByte & 0xf0) >> 4);
            System.out.println(mqttControlPacketType);
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
            message.setRemainingLength(remainingLength);

            AbstractMqttControlPacket packet;
            switch (mqttControlPacketType) {
                case CONNECT: {
                    packet = decodePacket(byteArrayInputStream, remainingLength);
                    break;
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

    private MqttConnectControlPacket decodePacket(ByteArrayInputStream byteArrayInputStream,
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

            byteArrayOutputStream.write(firstByte);
            
            byte [] extraBuffer = null;
            

            switch(message.getMqttControlPacketType()) {
                case CONNECT: {
                    extraBuffer = encodePacket((MqttConnectControlPacket)message.getPacket());
                    break;
                }
                case CONNACK: {
                    extraBuffer = encodePacket((MqttConnAckControlPacket)message.getPacket());
                    break;
                }
                default: {
                    break;
                }
            }
            if(extraBuffer != null) {
                message.setRemainingLength(extraBuffer.length);
                byteArrayOutputStream.write(message.getRemainingLength());
                byteArrayOutputStream.write(extraBuffer);
            }

            buffer = byteArrayOutputStream.toByteArray();
        } catch(Exception e) {
            e.printStackTrace();
        }
        ByteUtil.printBytesAsString(buffer);
		return buffer;
    }   
    
    /**
     * Encoder for connect message
     * @param message
     * @param byteArrayOutputStream
     */
    private byte [] encodePacket(MqttConnectControlPacket packet) {
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
            if(packet.getClientIdentifier() != null) {
                byteArrayOutputStream.write(0);
                byteArrayOutputStream.write(packet.getClientIdentifier().length());
                byteArrayOutputStream.write(packet.getClientIdentifier().getBytes());
            }

            /** Will topic */
            if(packet.getWillTopic() != null) {
                byteArrayOutputStream.write(0);
                byteArrayOutputStream.write(packet.getWillTopic().length());
                byteArrayOutputStream.write(packet.getWillTopic().getBytes());
            }

            /** Will message */
            if(packet.getWillMessage() != null) {
                byteArrayOutputStream.write(0);
                byteArrayOutputStream.write(packet.getWillMessage().length());
                byteArrayOutputStream.write(packet.getWillMessage().getBytes());
            }

            /** Username */
            if(packet.getUsername() != null) {
                byteArrayOutputStream.write(0);
                byteArrayOutputStream.write(packet.getUsername().length());
                byteArrayOutputStream.write(packet.getUsername().getBytes());
            }

            /** Password */
            if(packet.getPassword() != null) {
                byteArrayOutputStream.write(0);
                byteArrayOutputStream.write(packet.getPassword().length());
                byteArrayOutputStream.write(packet.getPassword().getBytes());
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
    private byte [] encodePacket(MqttConnAckControlPacket packet) {
        byte [] buffer = null;
    	try(ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
           
            /** Set Connect Acknowledge Flags */
            byteArrayOutputStream.write(packet.isSessionPresentFlag() ? 1 : 0);

            // Set return code
            byteArrayOutputStream.write(packet.getMqttConnectReturnCode().get());

            buffer = byteArrayOutputStream.toByteArray();
        } catch(Exception e) {
            e.printStackTrace();
        }
        return buffer;
    }
}
