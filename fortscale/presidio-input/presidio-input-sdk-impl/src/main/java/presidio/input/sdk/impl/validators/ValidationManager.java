package presidio.input.sdk.impl.validators;

import fortscale.domain.core.AbstractAuditableDocument;
import fortscale.utils.logging.Logger;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

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
                logger.warn("Validation for event with id {} failed. There were {} violations.", document.getId(), violations.size());
                for (ConstraintViolation<AbstractAuditableDocument> violation : violations) {
                    final Path propertyPath = violation.getPropertyPath();
                    final String message = violation.getMessage();
                    logger.debug("Violation occurred. Property: {}, Message: {}.", propertyPath, message);
                    invalidResults.add(new InvalidInputDocument(document, violations));
                }
            }
        }

        logger.info("{} out of {} records are valid.", validResults.size(), documents.size());
        ValidationResults validationResults = new ValidationResults(validResults, invalidResults);
        return validationResults;
    }

    public static class ValidationResults {
        public final List<? extends AbstractAuditableDocument> validDocuments;
        public final List<? extends InvalidInputDocument> invalidDocuments;

        public ValidationResults(List<? extends AbstractAuditableDocument> validDocuments, List<? extends InvalidInputDocument> invalidDocuments) {
            this.validDocuments = validDocuments;
            this.invalidDocuments = invalidDocuments;
        }
    }


    @Document
    @SuppressWarnings("PublicField")
    public static class InvalidInputDocument extends AbstractAuditableDocument {

        private static final String INVALID_DOCUMENT_FIELD_NAME = "invalidDocument";
        private static final String VIOLATIONS_FIELD_NAME = "violations";

        @Field(INVALID_DOCUMENT_FIELD_NAME)
        public AbstractAuditableDocument invalidDocument;

        @Field(VIOLATIONS_FIELD_NAME)
        public Set<ConstraintViolation<AbstractAuditableDocument>> violations;

        public InvalidInputDocument() {
        }

        public InvalidInputDocument(AbstractAuditableDocument invalidDocument, Set<ConstraintViolation<AbstractAuditableDocument>> violations) {
            this.invalidDocument = invalidDocument;
            this.violations = violations;
        }

        public AbstractAuditableDocument getInvalidDocument() {
            return invalidDocument;
        }

        public void setInvalidDocument(AbstractAuditableDocument invalidDocument) {
            this.invalidDocument = invalidDocument;
        }

        public Set<ConstraintViolation<AbstractAuditableDocument>> getViolations() {
            return violations;
        }

        public void setViolations(Set<ConstraintViolation<AbstractAuditableDocument>> violations) {
            this.violations = violations;
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this)
                    .append("invalidDocument", invalidDocument)
                    .append("violations", violations)
                    .toString();
        }
    }
}

