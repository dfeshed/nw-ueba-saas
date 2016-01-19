package fortscale.services.configuration.gds.state.field;

import fortscale.services.configuration.gds.state.Resettable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author gils
 * 17/01/2016
 */
public class FieldMetadataDictionary implements Resettable{
    private Map<String, FieldMetadata> fieldMetadataMap = new HashMap<>();

    private Map<FieldMetadata, FieldMetadata> fieldToScoredFieldMap = new HashMap<>();

    public void addField(FieldMetadata fmd) {
        fieldMetadataMap.put(fmd.getFieldName(), fmd);
    }

    public void pairFieldToScore(String fieldName, String scoreFieldName) {
        FieldMetadata fieldMetadata = fieldMetadataMap.get(fieldName);
        FieldMetadata scoreFieldMetadata = fieldMetadataMap.get(scoreFieldName);

        if (fieldMetadata == null) {
            throw new IllegalStateException("Could not find field metadata with name " + fieldName);
        }

        if (scoreFieldMetadata == null) {
            throw new IllegalStateException("Could not find score field metadata with name " + scoreFieldName);
        }

        fieldToScoredFieldMap.put(fieldMetadata, scoreFieldMetadata);
    }

    public Set<FieldMetadata> getAllFields() {
        return new HashSet<>(fieldMetadataMap.values());
    }

    public Map<FieldMetadata, FieldMetadata> getAllFieldToScorePairs() {
        return new HashMap<>(fieldToScoredFieldMap);
    }

    // given a normal field, return the corresponding score field (if any)
    public FieldMetadata getScoreFieldMetaData(FieldMetadata fieldMetadata){
        return fieldToScoredFieldMap.get(fieldMetadata);
    }

    public FieldMetadata getFieldMetadataByName(String name){
        return fieldMetadataMap.get(name);
    }


    @Override
    public void reset() {
        fieldMetadataMap.clear();
        fieldToScoredFieldMap.clear();
    }
}
