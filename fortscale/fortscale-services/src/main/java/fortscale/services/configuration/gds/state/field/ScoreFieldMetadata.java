package fortscale.services.configuration.gds.state.field;

/**
 * @author gils
 * 17/01/2016
 */
public class ScoreFieldMetadata extends BaseFieldMetadata{

    public ScoreFieldMetadata(String fieldName, boolean isInUse, boolean isAdditionalField) {
        super(fieldName, FieldType.DOUBLE, isInUse, isAdditionalField);
    }

    @Override
    public String toString() {
        return "ScoreFieldMetadata: " + super.toString();
    }
}
