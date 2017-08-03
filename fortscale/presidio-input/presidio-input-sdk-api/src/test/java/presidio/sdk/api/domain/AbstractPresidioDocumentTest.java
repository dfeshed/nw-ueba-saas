package presidio.sdk.api.domain;

import fortscale.domain.core.AbstractPresidioDocument;
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

public class AbstractPresidioDocumentTest {

    @Test
    public void testValidRecord() {
        AbstractPresidioDocument authenticationRawEvent = createEvent();
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<AbstractPresidioDocument>> violations = validator.validate(authenticationRawEvent);
        Assert.assertTrue(CollectionUtils.isEmpty(violations));
    }

    @Test
    public void testNoEventId() {
        AbstractPresidioDocument authenticationRawEvent = createEvent();
        authenticationRawEvent.setEventId(null);
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<AbstractPresidioDocument>> violations = validator.validate(authenticationRawEvent);
        Assert.assertEquals(1, violations.size());
        Assert.assertTrue(violations.iterator().next().getConstraintDescriptor().getAnnotation() instanceof NotEmpty);
    }

    @Test
    public void testNoDataSource() {
        AbstractPresidioDocument authenticationRawEvent = createEvent();
        authenticationRawEvent.setDataSource(null);
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<AbstractPresidioDocument>> violations = validator.validate(authenticationRawEvent);
        Assert.assertEquals(1, violations.size());
        Assert.assertTrue(violations.iterator().next().getConstraintDescriptor().getAnnotation() instanceof NotEmpty);
    }

    @Test
    public void testNoUserId() {
        AbstractPresidioDocument authenticationRawEvent = createEvent();
        authenticationRawEvent.setUserId(null);
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<AbstractPresidioDocument>> violations = validator.validate(authenticationRawEvent);
        Assert.assertEquals(1, violations.size());
        Assert.assertTrue(violations.iterator().next().getConstraintDescriptor().getAnnotation() instanceof NotEmpty);
    }

    @Test
    public void testNoOperationType() {
        AbstractPresidioDocument authenticationRawEvent = createEvent();
        authenticationRawEvent.setOperationType(null);
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<AbstractPresidioDocument>> violations = validator.validate(authenticationRawEvent);
        Assert.assertEquals(1, violations.size());
        Assert.assertTrue(violations.iterator().next().getConstraintDescriptor().getAnnotation() instanceof NotEmpty);
    }

    @Test
    public void testNoOperationTypeCategory() {
        AbstractPresidioDocument authenticationRawEvent = createEvent();
        authenticationRawEvent.setOperationTypeCategory(null);
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<AbstractPresidioDocument>> violations = validator.validate(authenticationRawEvent);
        Assert.assertTrue(CollectionUtils.isEmpty(violations));
    }

    @Test
    public void testNoUserName() {
        AbstractPresidioDocument authenticationRawEvent = createEvent();
        authenticationRawEvent.setUserName(null);
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<AbstractPresidioDocument>> violations = validator.validate(authenticationRawEvent);
        Assert.assertTrue(CollectionUtils.isEmpty(violations));
    }

    @Test
    public void testNoUserDisplayName() {
        AbstractPresidioDocument authenticationRawEvent = createEvent();
        authenticationRawEvent.setUserDisplayName(null);
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<AbstractPresidioDocument>> violations = validator.validate(authenticationRawEvent);
        Assert.assertTrue(CollectionUtils.isEmpty(violations));
    }

    @Test
    public void testNoAdditionalInfo() {
        AbstractPresidioDocument authenticationRawEvent = createEvent();
        authenticationRawEvent.setAdditionalInfo(null);
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<AbstractPresidioDocument>> violations = validator.validate(authenticationRawEvent);
        Assert.assertTrue(CollectionUtils.isEmpty(violations));
    }

    public AbstractPresidioDocument createEvent() {
        AbstractPresidioDocument abstractPresidioDocument = new AbstractPresidioDocument(Instant.now(), "eventId",
                "dataSource", "userId", "operationType", new ArrayList<>(),
                EventResult.SUCCESS, "userName", "userDisplayName", new HashMap<>());

        return abstractPresidioDocument;
    }


}
