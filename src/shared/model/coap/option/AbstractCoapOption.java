package shared.model.coap.option;

public abstract class AbstractCoapOption<T> {

    private Boolean _isCrucial;
    private Boolean _isUnsafe;
    private Boolean _isNoCacheKey;
    private Boolean _isRepeatable;
    private CoapOptionNumberEnum _number;
    protected T _value;

    public AbstractCoapOption(
        CoapOptionNumberEnum _number,  
        Boolean _isCrucial, 
        Boolean _isUnsafe, 
        Boolean _isNoCacheKey, 
        Boolean _isRepetable,
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
    public Boolean isCrucial() {
        return this._isCrucial;
    }
    public Boolean isUnsafe() {
        return this._isUnsafe;
    }
    public Boolean isNoCacheKey() {
        return this._isNoCacheKey;
    }
    public Boolean isRepetable() {
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
