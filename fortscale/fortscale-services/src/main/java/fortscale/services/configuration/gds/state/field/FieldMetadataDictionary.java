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
    //additional fields should have extra configuration in entities properties step
    private Map<String, FieldMetadata> additionalFieldMetaDataMap = new HashMap<>();

    private Map<String, ScoreFieldMetadata> scoreFieldMetadataMap = new HashMap<>();

    private Map<FieldMetadata, ScoreFieldMetadata> fieldToScoredFieldMap = new HashMap<>();

    public void addField(FieldMetadata fmd) {
        fieldMetadataMap.put(fmd.getFieldName(), fmd);
    }

    public void addScoreField(ScoreFieldMetadata smd) {
        scoreFieldMetadataMap.put(smd.getFieldName(), smd);
    }

    public void pairFieldToScore(FieldMetadata fieldMD, ScoreFieldMetadata scoreFieldMD) {
        pairFieldToScore(fieldMD.getFieldName(), scoreFieldMD.getFieldName());
    }

    public void pairFieldToScore(String fieldName, String scoreFieldName) {
        FieldMetadata fieldMetadata = fieldMetadataMap.get(fieldName);
        ScoreFieldMetadata scoreFieldMetadata = scoreFieldMetadataMap.get(scoreFieldName);

        if (fieldMetadata == null) {
            throw new IllegalStateException("Could not find field metadata with name " + fieldName);
        }

        if (scoreFieldMetadata == null) {
            throw new IllegalStateException("Could not find score field metadata with name " + scoreFieldName);
        }

        fieldToScoredFieldMap.put(fieldMetadata, scoreFieldMetadata);
    }

    public FieldMetadata getFieldMetadataByName(String fieldName) {
        return fieldMetadataMap.get(fieldName);
    }

    public ScoreFieldMetadata getScoreFieldMetadataByName(String fieldName) {
        return scoreFieldMetadataMap.get(fieldName);
    }

    public Set<FieldMetadata> getAllFields() {
        return new HashSet<>(fieldMetadataMap.values());
    }

    public Set<ScoreFieldMetadata> getAllScoreFields() {
        return new HashSet<>(scoreFieldMetadataMap.values());
    }

    public Map<FieldMetadata, ScoreFieldMetadata> getAllFieldToScorePairs() {
        return new HashMap<>(fieldToScoredFieldMap);
    }

    public void addAdditionalField(FieldMetadata field){
        additionalFieldMetaDataMap.put(field.getFieldName(),field);
    }

    public  Map<String, FieldMetadata> getAdditionalFieldMetaDataMap(){
        return additionalFieldMetaDataMap;
    }

    // given a normal field, return the corresponding score field (if any)
    public ScoreFieldMetadata getScoreFieldMetaData(FieldMetadata fieldMetadata){
        return fieldToScoredFieldMap.get(fieldMetadata);
    }

    @Override
    public void reset() {
        fieldMetadataMap.clear();
        scoreFieldMetadataMap.clear();
        fieldToScoredFieldMap.clear();
    }
}
