package presidio.input.sdk.impl.validators;

import fortscale.domain.core.AbstractAuditableDocument;
import fortscale.utils.logging.Logger;
import org.apache.commons.collections.CollectionUtils;

import javax.validation.ConstraintViolation;
import javax.validation.Path;
import javax.validation.Validator;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ValidationManager {

    private static final Logger logger = Logger.getLogger(ValidationManager.class);
    private Validator validator;

    public ValidationManager(Validator validator) {
        this.validator = validator;
    }

    public List<? extends AbstractAuditableDocument> validate(List<? extends AbstractAuditableDocument> documents) {

        List<AbstractAuditableDocument> result = new ArrayList<>();

        logger.info("Validating the records");

        for (AbstractAuditableDocument document : documents) {
            Set<ConstraintViolation<AbstractAuditableDocument>> violations = validator.validate(document);

            if (CollectionUtils.isEmpty(violations)) {
                result.add(document);
            } else {
                logger.warn("Validation for event with id {} failed. There were {} violations.", document.getId(), violations.size());
                for (ConstraintViolation<AbstractAuditableDocument> violation : violations) {
                    final Path propertyPath = violation.getPropertyPath();
                    final String message = violation.getMessage();
                    logger.debug("Violation occurred. Property: {}, Message: {}.", propertyPath, message);
                }
            }
        }

        logger.info("{} out of {} records are valid.", result.size(), documents.size());
        return result;
    }
}
