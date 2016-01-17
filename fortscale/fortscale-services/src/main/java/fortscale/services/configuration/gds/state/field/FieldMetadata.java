package fortscale.services.configuration.gds.state.field;

/**
 * Field metadata representation
 *
 * @author gils
 * 17/01/2016
 */
public class FieldMetadata extends BaseFieldMetadata{

    public FieldMetadata(String fieldName, FieldType fieldType) {
        super(fieldName, fieldType, true);
    }

    public FieldMetadata(String fieldName, FieldType fieldType, boolean isInUse) {
        super(fieldName, fieldType, isInUse);
    }
}
