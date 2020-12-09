package shared.model.mqtt;

import java.util.HashMap;
import java.util.Map;
// Quality of service
public enum MqttQoS {
    AT_MOST_ONCE(0),
    AT_LEAST_ONCE(1),
    EXACLY_ONCE(2);

    private int mqttType;

    private MqttQoS(int mqttType)  {
        this.mqttType = mqttType;
    }

    public int get() {
        return this.mqttType;
    }

    //Lookup table
    private static final Map<Integer, MqttQoS> lookup = new HashMap<>();

    //Populate the lookup table on loading time
    static
    {
        for(MqttQoS env : MqttQoS.values())
        {
            lookup.put(env.get(), env);
        }
    }
    
    //This method can be used for reverse lookup purpose
    public static MqttQoS get(int MqttQoS) 
    {
        return lookup.get(MqttQoS);
    }


}
