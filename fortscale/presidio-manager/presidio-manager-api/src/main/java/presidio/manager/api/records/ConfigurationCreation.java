package presidio.manager.api.records;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public abstract class ConfigurationCreation {

    private List<String> badParams;
    private List<String> missingParams;
    private boolean isStructureValid;

    public List<String> getBadParams() {
        return badParams;
    }

    public void badParamsAddKeys(List<String> keys) {
        badParams.addAll(keys);
    }

    public void missingParamsAddKeys(List<String> keys) {
        missingParams.addAll(keys);
    }

    public void missingParamsAddKeys(String key) {
        missingParams.add(key);
    }

    public void badParamsAddKey(String key) {
        badParams.add(key);
    }

    public List<String> getMissingParams() {
        return missingParams;
    }

    public void setMissingParams(List<String> missingParams) {
        this.missingParams = missingParams;
    }

    public boolean isStructureValid() {
        return isStructureValid;
    }

    public void setBadParams(List<String> badParams) {
        this.badParams = badParams;
    }

    public void setStructureValid(boolean structureValid) {
        isStructureValid = structureValid;
    }

    abstract void setKeyValue(String key, JsonNode value);

    abstract void checkStructure();

    public List<String> addPrefixToBadParams(String prefix, List<String> badParams) {
        List<String> prefixBadParams = new ArrayList<>();
        badParams.forEach(param -> {
            prefixBadParams.add(prefix + "/" + param);
        });
        return prefixBadParams;
    }

    public void createConfiguration(JsonNode node) {
        this.badParams = new ArrayList<>();
        this.missingParams = new ArrayList<>();
        Iterator<String> itr = node.fieldNames();
        String key;
        while (itr.hasNext()) {
            key = itr.next().toString();
            setKeyValue(key, node.get(key));
        }
        if (badParams.isEmpty())
            isStructureValid = true;
    }
}
