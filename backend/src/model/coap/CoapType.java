package src.model.coap;

import java.util.HashMap;
import java.util.Map;

public enum CoapType {
    CON(0), // CONfirmable
    NON(1), // NON-confirmable
    ACK(2), // ACKnowledgement
    ReS(3); // ReSeT

    private Integer coapType;

    private CoapType(Integer coapType) {
        this.coapType = coapType;
    }

    
    public Integer get() {
        return this.coapType;
    }

    //Lookup table
    private static final Map<Integer, CoapType> lookup = new HashMap<>();

    //Populate the lookup table on loading time
    static
    {
        for(CoapType env : CoapType.values())
        {
            lookup.put(env.get(), env);
        }
    }
    
    //This method can be used for reverse lookup purpose
    public static CoapType get(Integer CoapType) 
    {
        return lookup.get(CoapType);
    }
}