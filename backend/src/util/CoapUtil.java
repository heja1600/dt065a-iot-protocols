package src.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import src.model.coap.CoapMessage;
import src.model.coap.option.AbstractCoapOption;
import src.model.coap.option.CoapOptionNumberEnum;
import src.model.coap.option.CoapOptionUnknown;
import src.model.coap.option.CoapOptionUriPath;
import src.model.coap.option.CoapOptionUriPort;

public class CoapUtil {

    @SuppressWarnings({"serial", "unchecked"})
    private static Map<CoapOptionNumberEnum, Class<AbstractCoapOption<?>>> table = new HashMap() {
        {
            put(CoapOptionNumberEnum.URI_PATH, CoapOptionUriPath.class);
            put(CoapOptionNumberEnum.URI_PORT, CoapOptionUriPort.class);
        }
    };
    
    public static AbstractCoapOption<?> resolveOption(CoapOptionNumberEnum optionNumber, byte [] bytes) throws Exception {
        AbstractCoapOption<?> coapOption;
        if(!table.containsKey(optionNumber)) {
            System.out.println("CoapOptionResolver doesnt containt optionNumber, giving it a unknown option: " + optionNumber);
            coapOption = new CoapOptionUnknown(optionNumber, ByteUtil.byteArrayToString(bytes));
        } else {
            Type type = resolveType(optionNumber);
            Object value;
            if(type == Integer.class) {
                value = ByteUtil.byteArrayToInteger(bytes);
            } else if(type == String.class) {
                value = ByteUtil.byteArrayToString(bytes);
            } else {
                throw new Exception("Cannot parse CoapOption with number:" +optionNumber + " and type: " + type);
            }
            coapOption = table.get(optionNumber).getDeclaredConstructor((Class<?>)type).newInstance(value);
        }
        return coapOption;
    }

    public static Type resolveType(CoapOptionNumberEnum optionNumber) throws Exception{
        Type type = ((ParameterizedType)table.get(optionNumber).getGenericSuperclass()).getActualTypeArguments()[0];
        return type;
    }

    public static boolean isAvailable(CoapOptionNumberEnum optionNumber)   {
        return table.containsKey(optionNumber);
    }



    public static boolean isUrl(CoapMessage message, String ... uris) {
        ArrayList<AbstractCoapOption<?>> uriPaths =  message.getOptions().get(CoapOptionNumberEnum.URI_PATH);
        if(uris.length != uriPaths.size()) {
            return false;
        }
        for(int i = 0; i < uriPaths.size(); i++) {
            if(!uriPaths.get(i).getValue().equals(uris[i])){
                return false;
            }
        }
        return true;
    }
    public static String getUrl(CoapMessage message) {
        ArrayList<AbstractCoapOption<?>> uriPaths =  message.getOptions().get(CoapOptionNumberEnum.URI_PATH);
        if(uriPaths == null) {
            return null;
        }
        String url = "";
        for(int i = 0; i < uriPaths.size(); i++) {
            url += ((CoapOptionUriPath) uriPaths.get(i)).getValue();
            if(i > 0 && i != uriPaths.size() - 1) {
                url += "/";
            }
        }
        return url;
    }
}
