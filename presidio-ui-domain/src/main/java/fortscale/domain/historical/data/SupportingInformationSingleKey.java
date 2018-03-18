package fortscale.domain.historical.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Supporting information single key representation. usage example: histogram.
 * The creation of SupportingInformationSingleKey object is based on a singe key and [optionally] on a key identifier.
 * The key identifier should be used for internal use only while the key is exposed to the user.
 *
 * @author gils
 * Date: 05/08/2015
 */
public class SupportingInformationSingleKey implements SupportingInformationKey {

    protected String key;

    protected String keyIdentifier;

    public SupportingInformationSingleKey(String key) {
        this.key = key;
    }

    public SupportingInformationSingleKey(String key, String uniqueIdentifier) {
        this.key = key;
        this.keyIdentifier = uniqueIdentifier;
    }

    public String getKey() {
        return key;
    }

    /**
     * generates key representation to json serialization - in this case a list of one key.
     * @return
     */
    @Override
    public List<String> generateKey(){

        List<String> genKey = new ArrayList<>();
        genKey.add(key);
        return genKey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SupportingInformationSingleKey that = (SupportingInformationSingleKey) o;

        if (key != null ? !key.equals(that.key) : that.key != null) return false;
        return !(keyIdentifier != null ? !keyIdentifier.equals(that.keyIdentifier) : that.keyIdentifier != null);

    }

    @Override
    public int hashCode() {
        int result = key != null ? key.hashCode() : 0;
        result = 31 * result + (keyIdentifier != null ? keyIdentifier.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "SupportingInformationSingleKey{" +
                "key='" + key + '\'' +
                ", keyIdentifier='" + keyIdentifier + '\'' +
                '}';
    }
}