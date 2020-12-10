package shared.model.coap.option;

public class CoapOptionUnknown extends AbstractCoapOption<Object> {

    public CoapOptionUnknown(CoapOptionNumberEnum _number,  Object _value) {
        super(_number, null, null, null, null, _value);
    }

    @Override
    public void setValueOfBytes(byte[] bytes) {
        // TODO Auto-generated method stub

    }

    @Override
    public byte[] getValueAsBytes() {
        // TODO Auto-generated method stub
        return null;
    }
    
}
