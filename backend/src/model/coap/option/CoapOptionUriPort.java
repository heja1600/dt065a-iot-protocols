package src.model.coap.option;

import src.util.ByteUtil;

public class CoapOptionUriPort extends AbstractCoapOption<Integer> {

    public CoapOptionUriPort(Integer _value) {
        super(CoapOptionNumberEnum.URI_PORT, true, true, false, true, _value);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void setValueOfBytes(byte[] bytes) {
        this.setValue(ByteUtil.byteArrayToInteger(bytes));
    }

    @Override
    public byte[] getValueAsBytes() {
        return ByteUtil.integerToByteArray(getValue());
    }
}
