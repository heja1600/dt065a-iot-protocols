package shared.model;

public class CoapMessage {

    private CoapCode coapCode;
    private String uriPath;

    
    public CoapCode getCoapCode() {
        return this.coapCode;
    }

    public CoapMessage setCoapCode(CoapCode coapCode) {
        this.coapCode = coapCode;
        return this;
    }

    
    public String getUriPath() {
        return this.uriPath;
    }

    public CoapMessage setUriPath(String uriPath) {
        this.uriPath = uriPath;
        return this;
    }


}
