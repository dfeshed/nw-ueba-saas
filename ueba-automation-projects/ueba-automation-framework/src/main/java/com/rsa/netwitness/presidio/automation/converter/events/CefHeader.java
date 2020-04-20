package com.rsa.netwitness.presidio.automation.converter.events;

        import static java.util.Objects.requireNonNull;

public class CefHeader {
    public final String cefVendor;
    public final String cefProduct;
    public final String cefEventType;
    public final String cefEventDesc;

    public CefHeader(String cefVendor, String cefProduct, String cefEventType, String cefEventDesc) {
        this.cefVendor = requireNonNull(cefVendor);
        this.cefProduct = requireNonNull(cefProduct);
        this.cefEventType = requireNonNull(cefEventType);
        this.cefEventDesc = requireNonNull(cefEventDesc);
    }
}
