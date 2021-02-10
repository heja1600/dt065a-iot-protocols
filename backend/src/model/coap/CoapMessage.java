package model.coap;

import java.util.ArrayList;
import java.util.SortedMap;
import java.util.TreeMap;

import model.coap.option.AbstractCoapOption;
import model.coap.option.CoapOptionNumberEnum;

public class CoapMessage {

    private CoapCode code;
    private CoapType type;
    private int version;
    private String token;
    private int messageId;
    private String payload;
    private SortedMap<CoapOptionNumberEnum, ArrayList<AbstractCoapOption<?>>> options;

    public CoapMessage() {
        this.code = CoapCode.GET;
   
        this.version = 0x0;
        this.token = null;
        this.type = CoapType.NON;
        this.messageId = 0;
        this.options = new TreeMap<CoapOptionNumberEnum, ArrayList<AbstractCoapOption<?>>>();
        this.payload = null;
    }

    public CoapCode getCode() {
        return code;
    }

    public CoapMessage setCode(CoapCode code) {
        this.code = code;
        return this;
    }

    public CoapType getType() {
        return type;
    }

    public CoapMessage setType(CoapType type) {
        this.type = type;
        return this;
    }


    public int getVersion() {
        return this.version;
    }

    public CoapMessage setVersion(int version) throws Exception {
        if(version > 0x3 || version < 0x0) {
            throw new Exception();
        }
        this.version = version;
        return this;
    }

    public String getToken() {
        return token;
    }

    public CoapMessage setToken(String token) throws Exception {
        if(token.getBytes().length > 0xf) {
            throw new Exception("Token maximum length is 8 bytes");
        } 
        this.token = token;
        return this;
    }

    public int getMessageId() {
        return messageId;
    }

    public CoapMessage setMessageId(int messageId) throws Exception{
        if(messageId < 0x00 || messageId > 0xffff) {
            throw new Exception("Message id maximum length is 2 bytes, messageId: " + messageId);
        }
        this.messageId = messageId;
        return this;
    }

    @SuppressWarnings("serial")
    public CoapMessage addOption(AbstractCoapOption<?> option) throws Exception {

        /** If array is uninitialized -> create array and add*/
        if(!options.containsKey(option.getNumber())) {
            options.put(option.getNumber(), new ArrayList<AbstractCoapOption<?>>() {
                {
                    add(option);
                }
            });

        /** If array is set to not be repeatable and array is empty -> add */
        } else if(!option.isRepetable() && options.get(option.getNumber()).isEmpty()) {
            options.get(option.getNumber()).add(option);
        
        /** If option is set to be repeatable -> add */
        } else if(option.isRepetable()) {
            options.get(option.getNumber()).add(option);

        /** If none of following is required -> throw error */
        } else {
            throw new Exception("Cannot add option: " + option.getNumber());
        }
        return this;
    }
    public CoapMessage setOptions(SortedMap<CoapOptionNumberEnum, ArrayList<AbstractCoapOption<?>>> options) {
        this.options = options;
        return this;
    }
    public CoapMessage resetOptions() {
        this.options = new TreeMap<CoapOptionNumberEnum, ArrayList<AbstractCoapOption<?>>>();
        return this;
    }


    public ArrayList<AbstractCoapOption<?>> getOptionsAsArray() {
        ArrayList<AbstractCoapOption<?>> options = new ArrayList<>();
        this.options.values().forEach(array -> array.forEach(option -> options.add(option)));
        return options;
    }

    public SortedMap<CoapOptionNumberEnum, ArrayList<AbstractCoapOption<?>>> getOptions() {
        return this.options;
    }
    

    public String getPayload() {
        return payload;
    }

    public CoapMessage setPayload(String payload) {
        this.payload = payload;
        return this;
    }


    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("CoapMessage:" + "\n");
        builder.append("Coap code is: " + getCode() + "\n");
        builder.append("Coap version is: " + getVersion() + "\n");
        builder.append("Coap type is: " + getType() + "\n");
        builder.append("Coap messageId:" + getMessageId() + "\n");
        builder.append("Coap token: " + getToken() + "\n");
        builder.append("Coap payload: " + getPayload() + "\n");

        var options = getOptionsAsArray();

        options.forEach(option -> {
            builder.append("Coap option number: " + option.getNumber() +  " = " + option.getValue() + "\n");
        });
        
        System.out.print("\n");
        return builder.toString();
    }
}
