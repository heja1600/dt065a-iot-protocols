package shared.model;

import java.util.HashMap;
import java.util.Map;

public enum CoapCode {

    GET((byte)1),
    POST((byte)2),
    PUT((byte)3),
    DELETE((byte)4),

    BAD_REQUEST((byte)128);

    private byte coapCoade;

    private CoapCode(byte coapCoade)  {
        this.coapCoade = coapCoade;
    }

    
    public byte get() {
        return this.coapCoade;
    }

    //Lookup table
    private static final Map<String, CoapCode> lookup = new HashMap<>();

    //Populate the lookup table on loading time
    static
    {
        for(CoapCode env : CoapCode.values())
        {
            lookup.put("" +env.get(), env);
        }
    }
    
    //This method can be used for reverse lookup purpose
    public static CoapCode get(byte coapCode) 
    {
        return lookup.get("" + coapCode);
    }
}
