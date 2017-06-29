package presidio.input.sdk.impl.validators;

import fortscale.domain.core.AbstractAuditableDocument;
import org.apache.commons.collections.CollectionUtils;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ValidationManager {

    Validator validator;

    public ValidationManager() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    public List<? extends AbstractAuditableDocument> validate(List<? extends AbstractAuditableDocument> documents) {

        List<AbstractAuditableDocument> result = new ArrayList<>();

        for (AbstractAuditableDocument document : documents) {
            Set<ConstraintViolation<AbstractAuditableDocument>> violations = validator.validate(document);

            if (CollectionUtils.isEmpty(violations)) {
                result.add(document);
            }
        }
        return result;
    }
}
