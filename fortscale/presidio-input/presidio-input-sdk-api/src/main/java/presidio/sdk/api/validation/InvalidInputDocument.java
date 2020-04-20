package presidio.sdk.api.validation;

import fortscale.domain.core.AbstractAuditableDocument;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.ConstraintViolation;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by efratn on 16/01/2018.
 */
@Document
@SuppressWarnings("PublicField")
public class InvalidInputDocument extends AbstractAuditableDocument {

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
