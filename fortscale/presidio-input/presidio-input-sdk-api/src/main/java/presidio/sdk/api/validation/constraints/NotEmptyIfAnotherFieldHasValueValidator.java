package presidio.sdk.api.validation.constraints;


import fortscale.utils.logging.Logger;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import presidio.sdk.api.utils.ReflectionUtils;
import presidio.sdk.api.validation.NotEmptyIfAnotherFieldHasValue;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class NotEmptyIfAnotherFieldHasValueValidator implements ConstraintValidator<NotEmptyIfAnotherFieldHasValue, Object> {

    private static final Logger logger = Logger.getLogger(NotEmptyIfAnotherFieldHasValueValidator.class);

    private String fieldName;
    private String[] fieldValues;
    private String dependFieldName;

    @Override
    public void initialize(NotEmptyIfAnotherFieldHasValue notEmptyIfAnotherFieldHasValue) {
        fieldValues = notEmptyIfAnotherFieldHasValue.fieldValues();
        fieldName = notEmptyIfAnotherFieldHasValue.fieldName();
        dependFieldName = notEmptyIfAnotherFieldHasValue.dependFieldName();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null) {
            logger.info("Validation failed, the object received is null");
            return false;
        }

        Object fieldValue = ReflectionUtils.getFieldValue(value, fieldName);
        Object dependFieldValue = ReflectionUtils.getFieldValue(value, dependFieldName);

        if (ArrayUtils.contains(fieldValues, fieldValue) && (dependFieldValue == null || StringUtils.isEmpty(dependFieldValue.toString()))) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate(constraintValidatorContext.getDefaultConstraintMessageTemplate())
                    .addPropertyNode(dependFieldName)
                    .addConstraintViolation();
            logger.info("Validation failed - {} field cannot be empty if {} field value is one of {}", dependFieldName, fieldName, fieldValues);
            return false;
        }

        logger.debug("Validation passed");
        return true;
    }

}
