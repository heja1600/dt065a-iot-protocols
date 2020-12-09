package shared.model.mqtt;

import java.util.HashMap;
import java.util.Map;

public enum MqttMessageType {
    CONNECT(1),
    PUBLISH(3),
    SUBCRIBE(8),
    UNSUBSCRIBE(10),
    PINGREQ(12),
    PINGRESP(13),
    DISCONNECT(14);

    private int mqttType;

    private MqttMessageType(int mqttType)  {
        this.mqttType = mqttType;
    }

    
    public int get() {
        return this.mqttType;
    }

    //Lookup table
    private static final Map<Integer, MqttMessageType> lookup = new HashMap<>();

    //Populate the lookup table on loading time
    static
    {
        for(MqttMessageType env : MqttMessageType.values())
        {
            lookup.put(env.get(), env);
        }
    }
    
    //This method can be used for reverse lookup purpose
    public static MqttMessageType get(int MqttMessageType) 
    {
        return lookup.get( MqttMessageType);
    }


}
