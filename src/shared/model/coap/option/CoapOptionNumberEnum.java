package shared.model.coap.option;

import java.util.HashMap;
import java.util.Map;

public enum CoapOptionNumberEnum {
    UNKNOWN(-1),
    IF_MATCH(1),
    URI_HOST(3),
    E_TAG(4),
    IF_NONE_MATCH(5),
    URI_PORT(7),
    LOCATION_PATH(8),
    URI_PATH(11),
    CONTENT_FORMAT(12),
    MAX_AGE(14),
    URI_QUERY(15),
    ACCEPT(17),
    LOCATION_QUERY(20),
    SIZE_2(28),
    PROXY_URI(35),
    PROXY_SCHEME(39),
    SIZE_1(60);

    Integer optionNumber;

    CoapOptionNumberEnum(Integer optionNumber){
        this.optionNumber = optionNumber;
    }

    public Integer get() {
        return this.optionNumber;
    }

    //Lookup table
    private static final Map<Integer, CoapOptionNumberEnum> lookup = new HashMap<>();

    //Populate the lookup table on loading time
    static
    {
        for(CoapOptionNumberEnum env : CoapOptionNumberEnum.values())
        {
            lookup.put(env.get(), env);
        }
    }
    
    //This method can be used for reverse lookup purpose
    public static CoapOptionNumberEnum get(Integer optionNumber) 
    {
        return lookup.get(optionNumber);
    }
}
