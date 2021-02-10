package model.coap.option;

import util.ByteUtil;

public class CoapOptionUriPath extends AbstractCoapOption<String> {

    public CoapOptionUriPath(String _value) {
        super(CoapOptionNumberEnum.URI_PATH, true, true, false, true, _value);
    }

    @Override
    public void setValueOfBytes(byte[] bytes) {
        this.setValue(ByteUtil.byteArrayToString(bytes));
    }

    @Override
    public byte[] getValueAsBytes() {
        return getValue().getBytes();
    }
}
