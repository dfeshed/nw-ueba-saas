package presidio.input.sdk.impl.validators;

import fortscale.domain.core.AbstractAuditableDocument;
import fortscale.utils.logging.Logger;
import org.apache.commons.collections.CollectionUtils;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ValidationManager {

    private Validator validator;
    private static final Logger logger = Logger.getLogger(ValidationManager.class);

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
            }
        }

        logger.info("{} records are valid", result.size());
        return result;
    }
}
