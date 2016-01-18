package fortscale.services.configuration.gds.state.field;

import java.util.stream.Collectors;

/**
 * Helper class to extract values out of the field metadata dictionary
 *
 * @author gils
 * 18/01/2016
 */
public class FieldMetadataExtractor {

    private static final String COMMA_DELIMITER = ", ";
    private static final String SPACE_DELIMITER = " ";

    public static String extractBaseScoreFieldsCSV(FieldMetadataDictionary fieldMetadataDictionary) {
        return fieldMetadataDictionary.getAllScoreFields().stream().filter(field -> !field.isAdditionalField())
                .map(a -> a.getFieldName() + SPACE_DELIMITER + a.getFieldType()).collect(Collectors.joining(COMMA_DELIMITER));
    }

    public static String extractAdditionalScoreFieldsCSV(FieldMetadataDictionary fieldMetadataDictionary) {
        return fieldMetadataDictionary.getAllScoreFields().stream().filter(BaseFieldMetadata::isAdditionalField)
                .map(a -> a.getFieldName() + SPACE_DELIMITER + a.getFieldType()).collect(Collectors.joining(COMMA_DELIMITER));
    }

    public static String extractAdditionalFieldsCSV(FieldMetadataDictionary fieldMetadataDictionary) {
        return fieldMetadataDictionary.getRegularFields().stream().filter(BaseFieldMetadata::isAdditionalField)
                .map(a -> a.getFieldName() + SPACE_DELIMITER + a.getFieldType()).collect(Collectors.joining(COMMA_DELIMITER));
    }

    public static String extractAdditionalScoreFieldToFieldNameCSV(FieldMetadataDictionary fieldMetadataDictionary) {
        return fieldMetadataDictionary.getAllFieldToScorePairs().entrySet().stream().filter( a-> a.getKey().isAdditionalField())
                .map(a -> a.getValue().getFieldName() + SPACE_DELIMITER + a.getKey().getFieldName()).collect(Collectors.joining(COMMA_DELIMITER));
    }
}
