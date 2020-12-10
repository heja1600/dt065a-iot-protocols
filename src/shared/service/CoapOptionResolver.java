package shared.service;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import shared.model.coap.option.AbstractCoapOption;
import shared.model.coap.option.CoapOptionNumberEnum;
import shared.model.coap.option.CoapOptionUnknown;
import shared.model.coap.option.CoapOptionUriPath;
import shared.model.coap.option.CoapOptionUriPort;
import shared.util.ByteUtil;

public class CoapOptionResolver {

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
}
