package presidio.sdk.api.domain;

import fortscale.domain.core.EventResult;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.validator.constraints.NotEmpty;
import org.junit.Assert;
import org.junit.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class AbstractInputDocumentTest {

    @Test
    public void testValidRecord() {
        AbstractInputDocument authenticationRawEvent = createEvent();
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<AbstractInputDocument>> violations = validator.validate(authenticationRawEvent);
        Assert.assertTrue(CollectionUtils.isEmpty(violations));
    }

    @Test
    public void testNoEventId() {
        AbstractInputDocument authenticationRawEvent = createEvent();
        authenticationRawEvent.setEventId(null);
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<AbstractInputDocument>> violations = validator.validate(authenticationRawEvent);
        Assert.assertEquals(1, violations.size());
        Assert.assertTrue(violations.iterator().next().getConstraintDescriptor().getAnnotation() instanceof NotEmpty);
    }

    @Test
    public void testNoDataSource() {
        AbstractInputDocument authenticationRawEvent = createEvent();
        authenticationRawEvent.setDataSource(null);
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<AbstractInputDocument>> violations = validator.validate(authenticationRawEvent);
        Assert.assertEquals(1, violations.size());
        Assert.assertTrue(violations.iterator().next().getConstraintDescriptor().getAnnotation() instanceof NotEmpty);
    }

    @Test
    public void testNoUserId() {
        AbstractInputDocument authenticationRawEvent = createEvent();
        authenticationRawEvent.setUserId(null);
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<AbstractInputDocument>> violations = validator.validate(authenticationRawEvent);
        Assert.assertEquals(1, violations.size());
        Assert.assertTrue(violations.iterator().next().getConstraintDescriptor().getAnnotation() instanceof NotEmpty);
    }

    @Test
    public void testNoOperationType() {
        AbstractInputDocument authenticationRawEvent = createEvent();
        authenticationRawEvent.setOperationType(null);
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<AbstractInputDocument>> violations = validator.validate(authenticationRawEvent);
        Assert.assertEquals(1, violations.size());
        Assert.assertTrue(violations.iterator().next().getConstraintDescriptor().getAnnotation() instanceof NotEmpty);
    }

    @Test
    public void testNoOperationTypeCategory() {
        AbstractInputDocument authenticationRawEvent = createEvent();
        authenticationRawEvent.setOperationTypeCategories(null);
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<AbstractInputDocument>> violations = validator.validate(authenticationRawEvent);
        Assert.assertTrue(CollectionUtils.isEmpty(violations));
    }

    @Test
    public void testNoUserName() {
        AbstractInputDocument authenticationRawEvent = createEvent();
        authenticationRawEvent.setUserName(null);
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<AbstractInputDocument>> violations = validator.validate(authenticationRawEvent);
        Assert.assertTrue(CollectionUtils.isEmpty(violations));
    }

    @Test
    public void testNoUserDisplayName() {
        AbstractInputDocument authenticationRawEvent = createEvent();
        authenticationRawEvent.setUserDisplayName(null);
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<AbstractInputDocument>> violations = validator.validate(authenticationRawEvent);
        Assert.assertTrue(CollectionUtils.isEmpty(violations));
    }

    @Test
    public void testNoAdditionalInfo() {
        AbstractInputDocument authenticationRawEvent = createEvent();
        authenticationRawEvent.setAdditionalInfo(null);
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<AbstractInputDocument>> violations = validator.validate(authenticationRawEvent);
        Assert.assertTrue(CollectionUtils.isEmpty(violations));
    }

    public AbstractInputDocument createEvent() {
        AbstractInputDocument abstractInputDocument = new AbstractInputDocument(Instant.now(), "eventId",
                "dataSource", "userId", "operationType", new ArrayList<>(),
                EventResult.SUCCESS, "userName", "userDisplayName", new HashMap<>(), "resultCode");

        return abstractInputDocument;
    }


}
