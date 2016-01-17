package fortscale.services.configuration.gds.state.field;

import java.util.Map;
import java.util.Set;

/**
 * @author gils
 * 17/01/2016
 */
public class FieldMetadataTest {

    public static void main(String[] args) {
        FieldMetadataTest fieldMetadataTest = new FieldMetadataTest();

        FieldMetadataContainer fieldMetadataContainer = new FieldMetadataContainer();

        FieldMetadata countryField = new FieldMetadata("country", FieldType.STRING, true);
        FieldMetadata timeField = new FieldMetadata("time", FieldType.TIMESTAMP, true);
        FieldMetadata userField = new FieldMetadata("user", FieldType.STRING, true);

        fieldMetadataContainer.addField(countryField);
        fieldMetadataContainer.addField(timeField);

        ScoreFieldMetadata countryScore = new ScoreFieldMetadata("country_score", true);
        ScoreFieldMetadata timeScore = new ScoreFieldMetadata("time_score", true);

        fieldMetadataContainer.addScoreField(countryScore);
        fieldMetadataContainer.addScoreField(timeScore);

        fieldMetadataContainer.pairFieldToScore(countryField, countryScore);
        fieldMetadataContainer.pairFieldToScore(timeField, timeScore);

        Set<FieldMetadata> allFields = fieldMetadataContainer.getAllFields();
        Set<ScoreFieldMetadata> allScoreFields = fieldMetadataContainer.getAllScoreFields();

        Map<FieldMetadata, ScoreFieldMetadata> allFieldToScorePairs = fieldMetadataContainer.getAllFieldToScorePairs();
    }
}
