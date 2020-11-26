package shared.model.coap.option;

public abstract class AbstractCoapOption<T> {

    private boolean _isCrucial;
    private boolean _isUnsafe;
    private boolean _isNoCacheKey;
    private boolean _isRepeatable;
    private CoapOptionNumberEnum _number;
    protected T _value;

    public AbstractCoapOption(
        CoapOptionNumberEnum _number,  
        boolean _isCrucial, 
        boolean _isUnsafe, 
        boolean _isNoCacheKey, 
        boolean _isRepetable,
        T _value
    ) {
        this._isCrucial = _isCrucial;
        this._isUnsafe = _isUnsafe;
        this._isNoCacheKey = _isNoCacheKey;
        this._isRepeatable = _isRepetable;
        this._number = _number;
        this._value = _value;
    }

    public CoapOptionNumberEnum getNumber() {
        return this._number;
    }
    public boolean isCrucial() {
        return this._isCrucial;
    }
    public boolean isUnsafe() {
        return this._isUnsafe;
    }
    public boolean isNoCacheKey() {
        return this._isNoCacheKey;
    }
    public boolean isRepetable() {
        return this._isRepeatable;
    }
    public T getValue() {
        return this._value;
    }
    public void setValue(T _value) {
        this._value = _value;
    }

    public abstract void setValueOfBytes(byte [] bytes);

    public abstract byte [] getValueAsBytes();
}
