package presidio.sdk.api.validation.constraints;


import fortscale.utils.logging.Logger;
import org.apache.commons.lang.StringUtils;
import presidio.sdk.api.utils.ReflectionUtils;
import presidio.sdk.api.validation.FieldsMustHaveDifferentValues;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FieldsMustHaveDifferentValuesValidator implements ConstraintValidator<FieldsMustHaveDifferentValues, Object> {

    private static final Logger logger = Logger.getLogger(FieldsMustHaveDifferentValuesValidator.class);

    private String[] fieldNames;
    private Boolean canBeEmpty;

    @Override
    public void initialize(FieldsMustHaveDifferentValues fieldsMustHaveDifferentValues) {
        fieldNames = fieldsMustHaveDifferentValues.fieldNames();
        canBeEmpty = false;
        if (StringUtils.isNotEmpty(fieldsMustHaveDifferentValues.canBothBeEmpty())) {
            canBeEmpty = Boolean.valueOf(fieldsMustHaveDifferentValues.canBothBeEmpty());
        }
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null) {
            logger.info("Validation failed, the object received is null");
            return false;
        }

        List<Object> fieldsValue = new ArrayList<>();

        for (String fieldName : fieldNames) {
            fieldsValue.add(ReflectionUtils.getFieldValue(value, fieldName));
        }

        List<Object> uniqueValues = fieldsValue.stream().distinct().collect(Collectors.toList());

        if (uniqueValues.size() != fieldsValue.size()) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate(constraintValidatorContext.getDefaultConstraintMessageTemplate())
                    .addConstraintViolation();
            logger.info("Validation failed - {} fields cannot have the same values", fieldNames);
            return false;
        }

        logger.debug("Validation passed");
        return true;
    }
}
