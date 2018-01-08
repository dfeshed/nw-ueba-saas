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
import java.util.HashSet;
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
        public Set<Violation> violations;

        public InvalidInputDocument() {
        }

        public InvalidInputDocument(AbstractAuditableDocument invalidDocument, Set<ConstraintViolation<AbstractAuditableDocument>> violations) {
            this.violations = new HashSet<>();
            this.invalidDocument = invalidDocument;
            for (ConstraintViolation<AbstractAuditableDocument> violation : violations) {
                final String message = violation.getMessage();
                final String propertyPath = violation.getPropertyPath().toString();
                final String rootBeanClass = violation.getRootBeanClass().toString();
                final String messageTemplate = violation.getMessageTemplate();
                this.violations.add(new Violation(message, propertyPath, rootBeanClass, messageTemplate));
            }
        }

        public AbstractAuditableDocument getInvalidDocument() {
            return invalidDocument;
        }

        public void setInvalidDocument(AbstractAuditableDocument invalidDocument) {
            this.invalidDocument = invalidDocument;
        }

        public Set<Violation> getViolations() {
            return violations;
        }

        public void setViolations(Set<Violation> violations) {
            this.violations = violations;
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this)
                    .append("invalidDocument", invalidDocument)
                    .append("violations", violations)
                    .toString();
        }

        private static class Violation {
            private final String interpolatedMessage;
            private final String propertyPath;
            private final String rootBeanClass;
            private final String messageTemplate;

            public Violation(String interpolatedMessage, String propertyPath, String rootBeanClass, String messageTemplate) {
                this.interpolatedMessage = interpolatedMessage;
                this.propertyPath = propertyPath;
                this.rootBeanClass = rootBeanClass;
                this.messageTemplate = messageTemplate;
            }

            public String getInterpolatedMessage() {
                return interpolatedMessage;
            }

            public String getPropertyPath() {
                return propertyPath;
            }

            public String getRootBeanClass() {
                return rootBeanClass;
            }

            public String getMessageTemplate() {
                return messageTemplate;
            }

            @Override
            public String toString() {
                return new ToStringBuilder(this)
                        .append("interpolatedMessage", interpolatedMessage)
                        .append("propertyPath", propertyPath)
                        .append("rootBeanClass", rootBeanClass)
                        .append("messageTemplate", messageTemplate)
                        .toString();
            }
        }
    }
}

