package shared.model.coap;

public enum CoapOptionExtended {

    ZERO_BYTES(0),
    ONE_BYTE(1),
    TWO_BYTES(2);

    private Integer extended;
    CoapOptionExtended(Integer extended){
        this.extended = extended;
    }
    
    public static Integer getFourBitCode(CoapOptionExtended optionExtended, Integer number) throws Exception {
        switch(optionExtended) {
            case ZERO_BYTES: {
                return number;
            }
            case ONE_BYTE: {
                return 13;
            }
            case TWO_BYTES: {
                return 14;
            }
            default: {
                throw new Exception("Four bit code could not be represented from: " + optionExtended);
            }
        }
    }

   public static CoapOptionExtended getOptionExtended(Integer number) throws Exception{
        if(number > 0xff) {
            throw new Exception("Couldnt get OptionExtended from number: " + number);
        }
        return number <= 12 
        ? CoapOptionExtended.ZERO_BYTES
        : number <= 0xff
        ? CoapOptionExtended.ONE_BYTE
        : CoapOptionExtended.TWO_BYTES;
    }
}
