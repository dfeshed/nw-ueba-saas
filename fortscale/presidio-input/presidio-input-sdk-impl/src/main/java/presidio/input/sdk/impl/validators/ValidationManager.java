package presidio.input.sdk.impl.validators;

import fortscale.domain.core.AbstractAuditableDocument;
import fortscale.utils.logging.Logger;
import org.apache.commons.collections.CollectionUtils;
import presidio.sdk.api.validation.InvalidInputDocument;
import presidio.sdk.api.validation.ValidationResults;

import javax.validation.ConstraintViolation;
import javax.validation.Path;
import javax.validation.Validator;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static presidio.sdk.api.domain.AbstractInputDocument.EVENT_ID_FIELD_NAME;

public class ValidationManager {

    private static final Logger logger = Logger.getLogger(ValidationManager.class);
    private Validator validator;

    public ValidationManager(Validator validator) {
        this.validator = validator;
    }


    @SuppressWarnings("UnnecessaryLocalVariable")
    public ValidationResults validate(List<? extends AbstractAuditableDocument> documents) {

        List<AbstractAuditableDocument> validResults = new ArrayList<>();
        List<InvalidInputDocument> invalidResults = new ArrayList<>();


        logger.info("Validating the records");

        for (AbstractAuditableDocument document : documents) {
            Set<ConstraintViolation<AbstractAuditableDocument>> violations = validator.validate(document);

            if (CollectionUtils.isEmpty(violations)) {
                validResults.add(document);
            } else {
                try {
                    final java.lang.reflect.Field eventIdField = document.getClass().getField(EVENT_ID_FIELD_NAME);
                    final String eventIdValue = (String) eventIdField.get(document);
                    logger.warn("Validation for event with id {} and eventId {} failed. There were {} violations.", document.getId(), eventIdValue, violations.size());
                } catch (Exception e) {
                    logger.warn("Validation for event with id {} failed. There were {} violations.", document.getId(), violations.size());
                }
                invalidResults.add(new InvalidInputDocument(document, violations));
            }
        }

        logger.info("{} out of {} records are valid.", validResults.size(), documents.size());
        ValidationResults validationResults = new ValidationResults(validResults, invalidResults);
        return validationResults;
    }


}

