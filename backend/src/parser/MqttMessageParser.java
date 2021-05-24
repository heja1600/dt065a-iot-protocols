package src.parser;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import src.model.mqtt.MqttConnectFlag;
import src.model.mqtt.MqttControlPacketType;
import src.model.mqtt.MqttMessage;
import src.model.mqtt.MqttPacketIdentifier;
import src.model.mqtt.MqttQoS;
import src.model.mqtt.MqttTopic;
import src.model.mqtt.packet.AbstractMqttControlPacket;
import src.model.mqtt.packet.MqttConnAckControlPacket;
import src.model.mqtt.packet.MqttConnectControlPacket;
import src.model.mqtt.packet.MqttDisconnectControlPacket;
import src.model.mqtt.packet.MqttPingRequestPacket;
import src.model.mqtt.packet.MqttPingResponsePacket;
import src.model.mqtt.packet.MqttPubAckControlPacket;
import src.model.mqtt.packet.MqttPublishControlPacket;
import src.model.mqtt.packet.MqttSubAckControlPacket;
import src.model.mqtt.packet.MqttSubscribeControlPacket;
import src.model.mqtt.packet.MqttUnsubAckControlPacket;
import src.model.mqtt.packet.MqttUnsubscribeControlPacket;
import src.util.ByteUtil;

public class MqttMessageParser implements MessageParser<MqttMessage> {

    @Override
    public MqttMessage decode(byte[] buffer) {
  

        MqttMessage message = new MqttMessage();
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(buffer)) {

            int firstByte = byteArrayInputStream.read();

            // Get type
            MqttControlPacketType mqttControlPacketType = MqttControlPacketType.get((firstByte & 0xf0) >> 4);
            // If its a publish message, set otherwise reserved bits
            
            // set remaining length
            Integer remainingLength = byteArrayInputStream.read();
     
            message.setRemainingLength(remainingLength);

            AbstractMqttControlPacket packet;
            switch (mqttControlPacketType) {
                case CONNECT: {
                    packet = decodeConnectControlPacket(byteArrayInputStream, remainingLength);
                    break;
                }
                case DISCONNECT: {
                    packet = new MqttDisconnectControlPacket();
                    break;
                }
                case PUBLISH: {
                    packet = decodePublishControlPacket(byteArrayInputStream, remainingLength, firstByte);
                    break;
                }
                case SUBCRIBE: {
                    packet = decodeSubscribeControlPacket(byteArrayInputStream, remainingLength);
                    break;
                }
                case UNSUBSCRIBE: {
                    packet = decodeUnsubscribeControlPacket(byteArrayInputStream, remainingLength);
                    break;
                }
                case PINGREQ: {
                    packet = new MqttPingRequestPacket();
                    break;
                }
                case PINGRESP: {
                    packet = new MqttPingResponsePacket();
                    break;
                }
                case PUBACK: {
                    packet = new MqttPubAckControlPacket();
                    break;
                }
                case CONNACK: {
                    packet =  new MqttConnAckControlPacket();
                    break;
                }
                case SUBACK: {
                    packet = new MqttSubAckControlPacket();
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

    private MqttUnsubscribeControlPacket decodeUnsubscribeControlPacket(
        ByteArrayInputStream byteArrayInputStream, 
        Integer remainingLength
    ) throws IOException {
        MqttUnsubscribeControlPacket packet = new MqttUnsubscribeControlPacket();
        
        // Get packet identifier
        packet.setPacketIdentifier(decodePacketIdentifier(byteArrayInputStream, packet));
        remainingLength -= 2;

         /** Set topics */
        while(remainingLength > 0) {
            var length = twoBytesToInt(byteArrayInputStream);
            String topic = ByteUtil.byteArrayToString(
                byteArrayInputStream.readNBytes(
                    length
                )
            );
            remainingLength -= (length + 2);
            packet.addTopic(topic);
        }

        return packet;
    }

    private MqttPublishControlPacket decodePublishControlPacket(
        ByteArrayInputStream byteArrayInputStream, 
        Integer remainingLength,
        int firstByte
    ) throws IOException {
        MqttPublishControlPacket packet = new MqttPublishControlPacket()
            .setDUPFlag(((firstByte & 0b1000) >> 3) == 1)
            // Set Quality of Service (QOS) level
            .setMqttQoS(MqttQoS.get((firstByte & 0b0110) >> 1))
            // Set Retain
            .setRetainFlag((firstByte & 1) == 1);

        // Set topic
        var length = twoBytesToInt(byteArrayInputStream);
        var topic =  ByteUtil.byteArrayToString(byteArrayInputStream.readNBytes(length));
  
        packet.setTopic(topic);
        remainingLength -= (length + 2);
        
        // Set packet identifier
        if(packet.getMqttQoS() == MqttQoS.AT_LEAST_ONCE || packet.getMqttQoS() == MqttQoS.EXACLY_ONCE) {
            packet.setPacketIdentifier(decodePacketIdentifier(byteArrayInputStream, packet));
            remainingLength-=2;
        }

        if(remainingLength > 0) {
            packet.setPayload(ByteUtil.byteArrayToString(byteArrayInputStream.readNBytes(remainingLength)));
        }
        return packet;
    }

    private MqttSubscribeControlPacket decodeSubscribeControlPacket(
        ByteArrayInputStream byteArrayInputStream, 
        Integer remainingLength
    ) throws IOException {

        MqttSubscribeControlPacket packet = new MqttSubscribeControlPacket();

        /** Set packet identifier */
        packet.setPacketIdentifier(decodePacketIdentifier(byteArrayInputStream, packet));
        remainingLength -= 2;

        /** Set topics */
        while(remainingLength > 0) {
            var length = twoBytesToInt(byteArrayInputStream);
            String topic = ByteUtil.byteArrayToString(
                byteArrayInputStream.readNBytes(
                    length
                )
            );
            MqttQoS mqttQoS = MqttQoS.get(byteArrayInputStream.read());
            remainingLength -= (length + 3);
            packet.addTopic(new MqttTopic(topic, mqttQoS));
        }

        return packet;
    }
    
    private MqttConnectControlPacket decodeConnectControlPacket(
        ByteArrayInputStream byteArrayInputStream,
        Integer remainingLength
    ) throws Exception {

        MqttConnectControlPacket packet = new MqttConnectControlPacket();

        /** Set protocolname from variable header */
        byte[] variableHeader = byteArrayInputStream.readNBytes(twoBytesToInt(byteArrayInputStream));
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
                ByteUtil.byteArrayToString(byteArrayInputStream.readNBytes(twoBytesToInt(byteArrayInputStream)))
            );
        }

        if(packet.getConnectFlag().isWillFlag()) {
            packet.setWillTopic(
                ByteUtil.byteArrayToString(byteArrayInputStream.readNBytes(twoBytesToInt(byteArrayInputStream)))
            );
            packet.setWillMessage(
                ByteUtil.byteArrayToString(byteArrayInputStream.readNBytes(twoBytesToInt(byteArrayInputStream)))
            );
        }

        if(packet.getConnectFlag().isUserNameFlag()) {
            packet.setUsername(
                ByteUtil.byteArrayToString(byteArrayInputStream.readNBytes(twoBytesToInt(byteArrayInputStream)))
            );
        }

        if(packet.getConnectFlag().isPasswordFlag()) {
            packet.setPassword(
                ByteUtil.byteArrayToString(byteArrayInputStream.readNBytes(twoBytesToInt(byteArrayInputStream)))
            );
        }
        
        return packet;
    }

    private int twoBytesToInt(ByteArrayInputStream byteArrayInputStream) throws IOException {
        return (byteArrayInputStream.read() << 8) | byteArrayInputStream.read();
    }
    
    private int decodePacketIdentifier(ByteArrayInputStream byteArrayInputStream, MqttPacketIdentifier mqttPacketIdentifier) {
        return (byteArrayInputStream.read() << 8) | byteArrayInputStream.read();
    }
	@Override
	public byte[] encode(MqttMessage message) {
        byte [] buffer = null;
		try(ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {

            // Set Type
            int firstByte = message.getMqttControlPacketType().get() << 4;
            
            firstByte = firstByte | message.getPacket().getFixedHeaderFlags();
            
            byteArrayOutputStream.write(firstByte & 0xff);
            
            byte [] extraBuffer = null;
            

            switch(message.getMqttControlPacketType()) {
                case CONNECT: {
                    extraBuffer = encodePacket((MqttConnectControlPacket) message.getPacket());
                    break;
                }
                case CONNACK: {
                    extraBuffer = encodePacket((MqttConnAckControlPacket) message.getPacket());
                    break;
                }
                case SUBACK: {
                    extraBuffer = encodePacket((MqttSubAckControlPacket) message.getPacket());
                    break;
                }
                case PUBACK: {
                    extraBuffer = encodePacket((MqttPubAckControlPacket) message.getPacket());
                    break;
                }
                case UNSUBACK: {
                    extraBuffer = encodePacket((MqttUnsubAckControlPacket) message.getPacket());
                    break;
                }
                case PUBLISH: {
                    extraBuffer = encodePacket((MqttPublishControlPacket) message.getPacket());
                    break;
                }
                // following packets has no extra buffer
                case PINGREQ:
                case PINGRESP: 
                case DISCONNECT: {
                    break;
                }
                default: {
                    System.out.println("Encoder doesnt support packet: " + message.getMqttControlPacketType() + " yet.");
                    break;
                }
            }
            if(extraBuffer != null) {
                message.setRemainingLength(extraBuffer.length);
                byteArrayOutputStream.write(message.getRemainingLength());
                byteArrayOutputStream.write(extraBuffer);
            } else {
                byteArrayOutputStream.write(0);
            }

            buffer = byteArrayOutputStream.toByteArray();
 

        } catch(Exception e) {
            System.out.println("failed to encode with type: " + message.getMqttControlPacketType());
            e.printStackTrace();
        }
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
     * Encoder for Connect Acknownledgement message
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

    /**
     * Encoder for Subscribe Ack message
     * @param message
     * @param byteArrayOutputStream
     */
    private byte [] encodePacket(MqttSubAckControlPacket packet) {
        byte [] buffer = null;
    	try(ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
           
            // Set packet identifier
            encodePacketIdentifier(byteArrayOutputStream, packet);
            // Set Payload
            packet.getQoSs().forEach(qoS -> {
                byteArrayOutputStream.write(qoS.get());
            });
            
            buffer = byteArrayOutputStream.toByteArray();
        } catch(Exception e) {
            e.printStackTrace();
        }
        return buffer;
    }

    /**
     * Encoder for Publish Ack message
     * @param message
     * @param byteArrayOutputStream
     */
    private byte [] encodePacket(MqttPubAckControlPacket packet) {
        byte [] buffer = null;
    	try(ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
           
            // Set packet identifier
            encodePacketIdentifier(byteArrayOutputStream, packet);

            buffer = byteArrayOutputStream.toByteArray();
        } catch(Exception e) {
            e.printStackTrace();
        }
        return buffer;
    }

    /**
     * Encoder for Publish Ack message
     * @param message
     * @param byteArrayOutputStream
     */
    private byte [] encodePacket(MqttUnsubAckControlPacket packet) {
        byte [] buffer = null;
    	try(ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
           
            // Set packet identifier
            encodePacketIdentifier(byteArrayOutputStream, packet);

            buffer = byteArrayOutputStream.toByteArray();
        } catch(Exception e) {
            e.printStackTrace();
        }
        return buffer;
    }

    /**
     * Encoder for Publish Ack message
     * @param message
     * @param byteArrayOutputStream
     */
    private byte [] encodePacket(MqttPublishControlPacket packet) {
        byte [] buffer = null;
    	try(ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
           
             /** Set length and topic*/
            var topic = packet.getTopic();
            writeTwoBytes(byteArrayOutputStream, topic == null ? 0 : topic.length());
            
            if(topic != null) {
                byteArrayOutputStream.writeBytes(topic.getBytes());
            }

            if(packet.getMqttQoS() == MqttQoS.AT_LEAST_ONCE || packet.getMqttQoS() == MqttQoS.EXACLY_ONCE) {
                // Set packet identifier
                encodePacketIdentifier(byteArrayOutputStream, packet);
            }

            // Set payload
            byteArrayOutputStream.write(packet.getPayload() != null ? packet.getPayload().getBytes(StandardCharsets.UTF_8) : "".getBytes());

            buffer = byteArrayOutputStream.toByteArray();
            
        } catch(Exception e) {
            e.printStackTrace();
        }
        return buffer;
    }

    private void encodePacketIdentifier(ByteArrayOutputStream byteArrayOutputStream, MqttPacketIdentifier packetIdentifier) {
        writeTwoBytes(byteArrayOutputStream, packetIdentifier.getPacketIdentifier());
    }

    private void writeTwoBytes(ByteArrayOutputStream byteArrayOutputStream, int number) {
        byteArrayOutputStream.write((number & 0xff00) >> 8);
        byteArrayOutputStream.write(number & 0xff);
    }
}
