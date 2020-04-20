package presidio.data.domain;

public class RegistryEntry {
    private String key;
    private String keyGroup;
    private String valueName;

    public RegistryEntry(String key, String keyGroup, String valueName) {
        this.key = key;
        this.keyGroup = keyGroup;
        this.valueName = valueName;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getKeyGroup() {
        return keyGroup;
    }

    public void setKeyGroup(String keyGroup) {
        this.keyGroup = keyGroup;
    }

    public String getValueName() {
        return valueName;
    }

    public void setValueName(String valueName) {
        this.valueName = valueName;
    }

    @Override
    public String toString() {
        return "RegistryEntry{" +
                "key='" + key + '\'' +
                ", keyGroup='" + keyGroup + '\'' +
                ", valueName='" + valueName + '\'' +
                '}';
    }
}
