package model.coap;

import java.util.HashMap;
import java.util.Map;

public enum CoapCode {

    GET(1),
    POST(2),
    PUT(3),
    DELETE(4),

    VALID(67),

    BAD_REQUEST(128);


    private int coapCoade;

    private CoapCode(int coapCoade)  {
        this.coapCoade = coapCoade;
    }

    
    public int get() {
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
    public static CoapCode get(int coapCode) 
    {
        return lookup.get("" + coapCode);
    }
}
