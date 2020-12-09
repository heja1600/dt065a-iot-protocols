package shared.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Iterator;

import shared.model.coap.CoapCode;
import shared.model.coap.CoapMessage;
import shared.model.coap.CoapOptionExtended;
import shared.model.coap.CoapType;
import shared.model.coap.option.AbstractCoapOption;
import shared.model.coap.option.CoapOptionNumberEnum;
import shared.util.ByteUtil;



public class CoapMessageParser implements MessageParser<CoapMessage>{
  
	@Override
	public CoapMessage decode(byte[] buffer) {
        CoapMessage coapMessage = new CoapMessage();
        try(ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(buffer)) {

            int firstByte = byteArrayInputStream.read();



            /** Set version */
            coapMessage.setVersion((firstByte & 0xc0) >> 6); // 1100 0000

            /** Set type */
            coapMessage.setType(CoapType.get((firstByte & 0x30) >> 4)); // 0011 0000


            /** Set code */
            coapMessage.setCode(CoapCode.get((byte)byteArrayInputStream.read()));
            
            /** Set Message id */
            coapMessage.setMessageId(ByteUtil.byteArrayToInteger(byteArrayInputStream.readNBytes(2)) & 0xffff);
            
            System.out.println(coapMessage.getMessageId());
            /** Set token */
            int tokenLength = firstByte & 0xf; // 0000 1111
            coapMessage.setToken(new String(byteArrayInputStream.readNBytes(tokenLength)));
  
            /** Parse the options */
            parseOptions(0, 0, byteArrayInputStream, coapMessage);
         
            /** Parse payload */
            coapMessage.setPayload(new String(byteArrayInputStream.readAllBytes()));
     
        } catch(Exception e) {
            e.printStackTrace();
        }
 
        /** Get coap code */
        return coapMessage;
	}

	@Override
	public byte[] encode(CoapMessage message) {
		byte[] buffer;
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {

            /** Setting version and message type */
            int firstByte = (((message.getVersion() << 2) | message.getType().get()) << 4);

            /** Set token length */
            if (message.getToken() != null) {
                firstByte |= message.getToken().getBytes().length;
            }
            byteArrayOutputStream.write(firstByte);

            /** Set response code or method */
            if (message.getCode() != null) {
                int coapMethod = message.getCode().get();
                byteArrayOutputStream.write(coapMethod);
            }
  
            /** Set message id */
            byteArrayOutputStream.write((message.getMessageId() & 0xff00) >> 8);
            byteArrayOutputStream.write(message.getMessageId() & 0xff);
            
            /** Set Token */
            if(message.getToken() != null) {
                byte [] tokenBytes = message.getToken().getBytes();
                byteArrayOutputStream.write(tokenBytes, 0, tokenBytes.length);
            }
    
            /** Set all options */
            var options = message.getOptions().values().iterator();
            createOption(0, options, byteArrayOutputStream);
            
            /** Set payload byte to 1111 1111 */
            byteArrayOutputStream.write(0xff);
            if(message.getPayload() != null) {
                byteArrayOutputStream.write(message.getPayload().getBytes());
            }

            buffer = byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }


        /** set Code (Method) */
        return buffer;
	}

    private void createOption(
        Integer precedingOptionNumber, 
        Iterator<ArrayList<AbstractCoapOption<?>>> iterator, 
        ByteArrayOutputStream byteArrayOutputStream
    ) throws Exception {
        if(!iterator.hasNext()) {
            return;
        }

        Iterator<AbstractCoapOption<?>> options = iterator.next().iterator();
        
        AbstractCoapOption<?> firstOption = options.next();
        Integer optionNumber = firstOption.getNumber().get();
        /** If its the first, option delta will be same as option number */

        var optionDelta = optionNumber + precedingOptionNumber;
        byte [] optionValue = optionValueToBytes(firstOption);

        /** Option delta & Option length  */
        CoapOptionExtended deltaExtended = CoapOptionExtended.getOptionExtended(optionDelta);
        CoapOptionExtended lengthExtended = CoapOptionExtended.getOptionExtended(optionValue.length);

        byteArrayOutputStream.write((
            CoapOptionExtended.getFourBitCode(deltaExtended, optionDelta) << 4) 
            | CoapOptionExtended.getFourBitCode(lengthExtended, optionValue.length)
        );

        /** Give Option delta more space if it needs it*/
        if(deltaExtended != CoapOptionExtended.ZERO_BYTES) {
            byteArrayOutputStream.write(ByteUtil.integerToByteArray(optionDelta));
        }

        if(lengthExtended != CoapOptionExtended.ZERO_BYTES) {
            byteArrayOutputStream.write(ByteUtil.integerToByteArray(optionValue.length));
        }

        byteArrayOutputStream.write(optionValue);
        /**
         * Resolve the rest of the options with a Option Delta as 0
         */
        while(options.hasNext()) {
            var option = options.next();
            optionValue = optionValueToBytes(option);
            lengthExtended = CoapOptionExtended.getOptionExtended(optionValue.length);
            byteArrayOutputStream.write(CoapOptionExtended.getFourBitCode(lengthExtended, optionValue.length));
            if(lengthExtended != CoapOptionExtended.ZERO_BYTES) {
                byteArrayOutputStream.write(ByteUtil.integerToByteArray(optionValue.length));
            }
            byteArrayOutputStream.write(optionValue);
        }

        createOption(optionNumber, iterator, byteArrayOutputStream);
    }

    private byte [] optionValueToBytes(AbstractCoapOption<?> option) throws Exception {
        byte [] optionValue;
        if(option.getValue() instanceof String) {
            optionValue = ((String) option.getValue()).getBytes();
        } else if(option.getValue() instanceof Integer) {
            optionValue = ByteUtil.integerToByteArray(((Integer) option.getValue()));
        } else {
            throw new Exception("Coudnt create bytes from option value: " + option.getValue());
        }
        return optionValue;
    }
    /**
     * 
     * @param buffer
     * @return
     */
    public CoapMessage parseCoapMessage(byte[] buffer) {
        CoapMessage coapMessage = new CoapMessage();
        try(ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(buffer)) {

            int firstByte = byteArrayInputStream.read();

            /** Set version */
            coapMessage.setVersion((firstByte & 0xc0) >> 6); // 1100 0000

            /** Set type */
            coapMessage.setType(CoapType.get((firstByte & 0x30) >> 4)); // 0011 0000

            /** Set code */
            coapMessage.setCode(CoapCode.get((byte)byteArrayInputStream.read()));
            
            /** Set Message id */
            coapMessage.setMessageId(ByteUtil.byteArrayToInteger(byteArrayInputStream.readNBytes(2)) & 0xffff);

            /** Set token */
            int tokenLength = firstByte & 0xf; // 0000 1111
            coapMessage.setToken(new String(byteArrayInputStream.readNBytes(tokenLength)));
  
            /** Parse the options */
            parseOptions(0, 0, byteArrayInputStream, coapMessage);
         
            /** Parse payload */
            coapMessage.setPayload(new String(byteArrayInputStream.readAllBytes()));
     
        } catch(Exception e) {
            e.printStackTrace();
        }
 
        /** Get coap code */
        return coapMessage;
    }
    
    /**
     * 
     * @param precedingDeltaOption
     * @param precedingOptionNumber
     * @param byteArrayInputStream
     * @param coapMessage
     * @throws Exception
     */
    private void parseOptions(
        int precedingDeltaOption, 
        int precedingOptionNumber, 
        ByteArrayInputStream byteArrayInputStream,
        CoapMessage coapMessage
    ) throws Exception {

        var firstByte = byteArrayInputStream.read();
        /** Option delta */
        var deltaOption = (firstByte & 0xf0) >> 4; // 1111 0000
        
        int optionNumber;

        /** If its same type of option as previous, use previous option delta*/
        var sameOptionNumber = false;
        if(deltaOption == 0) {
            optionNumber = precedingOptionNumber;
        } else if(deltaOption == 13) {
            /** 
             * 13: 8-bit unsigned integer follows the initial byte and
             * indicates the Option Delta minus 13. 
             */
            optionNumber = ByteUtil.byteArrayToInteger(byteArrayInputStream.readNBytes(1)) - precedingOptionNumber;

        } else if(deltaOption == 14) {
            /** 
             * 14: A 16-bit unsigned integer in network byte order follows 
             * the initial byte and indicates the Option Delta minus 269.
             */

            optionNumber = ByteUtil.byteArrayToInteger(byteArrayInputStream.readNBytes(2)) - precedingOptionNumber;
            
        } else if(deltaOption == 15) { 
            /** 
             * 15: Reserved for the Payload Marker.  If the field is 
             * set to this value but the entire byte is not the payload 
             * marker, this MUSTbe processed as a message format error.
             */
            return;
        } else {
            /**
             * Option Delta:  4-bit unsigned integer.  A value 
             * between 0 and 12 indicates the Option Delta. Three 
             * values are reserved for special constructs:
             */  
            optionNumber = deltaOption - precedingOptionNumber;
        }

        var optionValueLength = (firstByte & 0x0f); // 0000 1111

        if(optionValueLength == 13) {
            /** 
             * 13: An 8-bit unsigned integer precedes the Option Value and
             * indicates the Option Length minus 13.
             */
            optionValueLength = byteArrayInputStream.read();
            
        } else if(optionValueLength == 14) {
            /** 
             * 14: A 16-bit unsigned integer in network byte order precedes the
             * Option Value and indicates the Option Length minus 269.
             */
            optionValueLength = ByteUtil.byteArrayToInteger(byteArrayInputStream.readNBytes(2));
            
        } else if(optionValueLength == 15) { 
            /** 
             * 15: Reserved for future use.  If the field is set to this value,
             * it MUST be processed as a message format error
             */
            throw new Exception("15 in Option Value Length is reserver for future use");
        }

        byte [] optionValue = byteArrayInputStream.readNBytes(optionValueLength);
        CoapOptionNumberEnum coapOptionNumber = CoapOptionNumberEnum.get(optionNumber);
        if(coapOptionNumber == null) {
            throw new Exception("Cannot get coapOption number from null");
        }
        
        AbstractCoapOption<?> option = CoapOptionResolver.resolveOption(coapOptionNumber, optionValue);
        coapMessage.addOption(option);
        parseOptions(
            sameOptionNumber ? precedingDeltaOption : deltaOption , 
            sameOptionNumber ? precedingOptionNumber : optionNumber,
            byteArrayInputStream, 
            coapMessage
        );
    }



    public static void printCoapMessage(CoapMessage coapMessage) {
        CoapMessageParser parser = new CoapMessageParser();
        byte [] bytes = parser.encode(coapMessage);
        System.out.println("Bits: \n");
        ByteUtil.printBytesAsString(bytes);
        System.out.println("\n");
        System.out.println("Coap code is: " + coapMessage.getCode());
        System.out.println("Coap version is: " + coapMessage.getVersion());
        System.out.println("Coap type is: " + coapMessage.getType());
        System.out.println("Coap messageId:" + coapMessage.getMessageId());
        System.out.println("Coap token: " + coapMessage.getToken());

        var options = coapMessage.getOptionsAsArray();
        options.forEach(option -> {
            System.out.println("Coap option number: " + option.getNumber() +  " = " + option.getValue());
        });
        
        System.out.println("Coap payload: " + coapMessage.getPayload());
        System.out.print("\n");
    }
}
