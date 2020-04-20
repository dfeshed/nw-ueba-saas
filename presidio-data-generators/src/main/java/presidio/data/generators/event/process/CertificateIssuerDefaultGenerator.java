package presidio.data.generators.event.process;

import presidio.data.generators.common.CyclicValuesGenerator;
import presidio.data.generators.common.IStringGenerator;

public class CertificateIssuerDefaultGenerator extends CyclicValuesGenerator<String> implements ICertificateIssuerGenerator {
    private final static String[] DEFAULT_VALUES = {"IdenTrust", "Comodo", "DigiCert", "GoDaddy", "Certum","Actalis", "Entrust", "Secom", "Let's Encrypt", "Trustwave", "Network Solutions"};

    public CertificateIssuerDefaultGenerator() {
        super(DEFAULT_VALUES);
    }

    @Override
    public String getNext() {
        return super.getNext();
    }
}
