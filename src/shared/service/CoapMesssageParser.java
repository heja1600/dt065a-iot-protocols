package shared.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Iterator;

import shared.model.coap.CoapCode;
import shared.model.coap.CoapMessage;
import shared.model.coap.CoapType;
import shared.model.coap.option.AbstractCoapOption;
import shared.model.coap.option.CoapOptionNumberEnum;
import shared.util.ByteUtil;



public class CoapMesssageParser {
    private enum OptionExtended {
        ZERO(0),
        ONE_BYTE(1),
        TWO_BYTES(2);
        private Integer extended;
        OptionExtended(Integer extended){
            this.extended = extended;
        }
    }
    private Integer getFourBitCode(OptionExtended optionExtended, Integer number) throws Exception {
        switch(optionExtended) {
            case ZERO: {
                return number;
            }
            case ONE_BYTE: {
                return 13;
            }
            case TWO_BYTES: {
                return 14;
            }
            default: {
                throw new Exception("Four bit code could not be represented from: " + optionExtended);
            }
        }
    }

    private OptionExtended getOptionExtended(Integer number) throws Exception{
        if(number > 0xff) {
            throw new Exception("Couldnt get OptionExtended from number: " + number);
        }
        return number <= 12 
        ? OptionExtended.ZERO
        : number <= 0xff
        ? OptionExtended.ONE_BYTE
        : OptionExtended.TWO_BYTES;
    }

    /**
     * 
     * @param coapMessage
     * @return
     */
    public byte[] createCoapMessage(CoapMessage coapMessage) {
        byte[] buffer;
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {

            /** Setting version and message type */
            int firstByte = (((coapMessage.getVersion() << 2) | coapMessage.getType().get()) << 4);

            /** Set token length */
            if (coapMessage.getToken() != null) {
                firstByte |= coapMessage.getToken().getBytes().length;
            }
            byteArrayOutputStream.write(firstByte);

            /** Set response code or method */
            if (coapMessage.getCode() != null) {
                int coapMethod = coapMessage.getCode().get();
                byteArrayOutputStream.write(coapMethod);
            }

            /** Set message id */
            byteArrayOutputStream.write(coapMessage.getMessageId() & 0xf0 >> 0xf);
            byteArrayOutputStream.write((coapMessage.getMessageId() & 0xf));
            
            /** Set Token */
            if(coapMessage.getToken() != null) {
                byte [] tokenBytes = coapMessage.getToken().getBytes();
                byteArrayOutputStream.write(tokenBytes, 0, tokenBytes.length);
            }
    
            /** Set all options */
            var options = coapMessage.getOptions().values().iterator();
            createOption(0, options, byteArrayOutputStream);
            
            /** Set payload byte to 1111 1111 */
            byteArrayOutputStream.write(0xff);
            if(coapMessage.getPayload() != null) {
                byteArrayOutputStream.write(coapMessage.getPayload().getBytes());
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
        OptionExtended deltaExtended = getOptionExtended(optionDelta);
        OptionExtended lengthExtended = getOptionExtended(optionValue.length);
        byteArrayOutputStream.write((getFourBitCode(deltaExtended, optionDelta) << 4) | getFourBitCode(lengthExtended, optionValue.length));

        /** Give Option delta more space if it needs it*/
        if(deltaExtended != OptionExtended.ZERO) {
            byteArrayOutputStream.write(ByteUtil.integerToByteArray(optionDelta));
        }

        if(lengthExtended != OptionExtended.ZERO) {
            byteArrayOutputStream.write(ByteUtil.integerToByteArray(optionValue.length));
        }

        byteArrayOutputStream.write(optionValue);
        /**
         * Resolve the rest of the options with a Option Delta as 0
         */
        while(options.hasNext()) {
            var option = options.next();
            optionValue = optionValueToBytes(option);
            lengthExtended = getOptionExtended(optionValue.length);
            byteArrayOutputStream.write(getFourBitCode(lengthExtended, optionValue.length));
            if(lengthExtended != OptionExtended.ZERO) {
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

            /** Set code */
            coapMessage.setCode(CoapCode.get((byte)byteArrayInputStream.read()));

            /** Set version */
            coapMessage.setVersion((firstByte & 0xc0) >> 6); // 1100 0000

            /** Set type */
            coapMessage.setType(CoapType.get((firstByte & 0x30) >> 4)); // 0011 0000

            /** Set Message id */
            coapMessage.setMessageId(ByteUtil.byteArrayToInteger(byteArrayInputStream.readNBytes(2)));
 
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
 
        printCoapMessage(coapMessage);
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
        System.out.println("Preceding Delta Option: " + precedingDeltaOption);
        System.out.println("precedingOptionNumber: " + precedingOptionNumber);
        System.out.println("deltaOption: " + precedingOptionNumber);
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
