package src.model.mqtt;

import java.util.HashMap;
import java.util.Map;

public enum MqttControlPacketType {
    CONNECT(1),
    CONNACK(2),
    PUBLISH(3),
    PUBACK(4),
    SUBCRIBE(8),
    SUBACK(9),
    UNSUBSCRIBE(10),
    UNSUBACK(11),
    PINGREQ(12),
    PINGRESP(13),
    DISCONNECT(14);

    private int mqttType;

    private MqttControlPacketType(int mqttType)  {
        this.mqttType = mqttType;
    }

    
    public int get() {
        return this.mqttType;
    }

    //Lookup table
    private static final Map<Integer, MqttControlPacketType> lookup = new HashMap<>();

    //Populate the lookup table on loading time
    static
    {
        for(MqttControlPacketType env : MqttControlPacketType.values())
        {
            lookup.put(env.get(), env);
        }
    }
    
    //This method can be used for reverse lookup purpose
    public static MqttControlPacketType get(int MqttMessageType) 
    {
        return lookup.get( MqttMessageType);
    }
}
