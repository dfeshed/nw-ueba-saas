package fortscale.domain.historical.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Supporting information key consist of two keys. usage example: heat-map.
 * The creation of SupportingInformationDualKey object is based on 2 keys (key 1, key 2) and [optionally] on a key identifier.
 * The key identifier should be used for internal use only while key1/key2 are exposed to the user.
 *
 * @author gils
 * Date: 05/08/2015
 */
public class SupportingInformationDualKey implements SupportingInformationKey {

    private String key1;
    private String key2;
    private String keyIdentifier;

    public SupportingInformationDualKey(String key1, String key2) {
        this.key1 = key1;
        this.key2 = key2;
    }

    public SupportingInformationDualKey(String key1, String key2, String keyIdentifier) {
        this.key1 = key1;
        this.key2 = key2;
        this.keyIdentifier = keyIdentifier;
    }

    public String getKey1() {
        return key1;
    }

    public String getKey2() {
        return key2;
    }

    /**
     * generates key representation to json serialization - in this case a list of two keys
     * @return
     */
    @Override
    public List<String> generateKey(){

       List<String> genKey = new ArrayList<>();
        genKey.add(key1);
        genKey.add(key2);
        return genKey;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SupportingInformationDualKey that = (SupportingInformationDualKey) o;

        if (key1 != null ? !key1.equals(that.key1) : that.key1 != null) return false;
        if (key2 != null ? !key2.equals(that.key2) : that.key2 != null) return false;
        return !(keyIdentifier != null ? !keyIdentifier.equals(that.keyIdentifier) : that.keyIdentifier != null);

    }

    @Override
    public int hashCode() {
        int result = key1 != null ? key1.hashCode() : 0;
        result = 31 * result + (key2 != null ? key2.hashCode() : 0);
        result = 31 * result + (keyIdentifier != null ? keyIdentifier.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "SupportingInformationDualKey{" +
                "key1='" + key1 + '\'' +
                ", key2='" + key2 + '\'' +
                ", keyIdentifier='" + keyIdentifier + '\'' +
                '}';
    }
}