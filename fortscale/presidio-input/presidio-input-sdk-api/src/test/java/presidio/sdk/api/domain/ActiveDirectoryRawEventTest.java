package presidio.sdk.api.domain;

import fortscale.domain.core.EventResult;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.validator.constraints.NotEmpty;
import org.junit.Assert;
import org.junit.Test;
import presidio.sdk.api.domain.rawevents.ActiveDirectoryRawEvent;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.Instant;
import java.util.Set;

public class ActiveDirectoryRawEventTest {

    @Test
    public void testValidRecord() {
        ActiveDirectoryRawEvent activeDirectoryRawEvent = createEvent();
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<ActiveDirectoryRawEvent>> violations = validator.validate(activeDirectoryRawEvent);
        Assert.assertTrue(CollectionUtils.isEmpty(violations));
    }

    @Test
    public void testNoObjectId() {
        ActiveDirectoryRawEvent activeDirectoryRawEvent = createEvent();
        activeDirectoryRawEvent.setObjectId(null);
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<ActiveDirectoryRawEvent>> violations = validator.validate(activeDirectoryRawEvent);
        Assert.assertTrue(CollectionUtils.isEmpty(violations));

    }

    @Test
    public void testNoUserId() {
        ActiveDirectoryRawEvent activeDirectoryRawEvent = createEvent();
        activeDirectoryRawEvent.setUserId(null);
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<AbstractInputDocument>> violations = validator.validate(activeDirectoryRawEvent);
        Assert.assertEquals(1, violations.size());
        Assert.assertTrue(violations.iterator().next().getConstraintDescriptor().getAnnotation() instanceof NotEmpty);
    }

    @Test
    public void testNoOperationType() {
        ActiveDirectoryRawEvent activeDirectoryRawEvent = createEvent();
        activeDirectoryRawEvent.setOperationType(null);
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<AbstractInputDocument>> violations = validator.validate(activeDirectoryRawEvent);
        Assert.assertEquals(1, violations.size());
        Assert.assertTrue(violations.iterator().next().getConstraintDescriptor().getAnnotation() instanceof NotEmpty);
    }

    @Test
    public void testNoOperationTypeCategory() {
        ActiveDirectoryRawEvent activeDirectoryRawEvent = createEvent();
        activeDirectoryRawEvent.setOperationTypeCategories(null);
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<AbstractInputDocument>> violations = validator.validate(activeDirectoryRawEvent);
        Assert.assertTrue(CollectionUtils.isEmpty(violations));
    }

    @Test
    public void testNoUserName() {
        ActiveDirectoryRawEvent activeDirectoryRawEvent = createEvent();
        activeDirectoryRawEvent.setUserName(null);
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<AbstractInputDocument>> violations = validator.validate(activeDirectoryRawEvent);
        Assert.assertTrue(CollectionUtils.isEmpty(violations));
    }

    @Test
    public void testNoUserDisplayName() {
        ActiveDirectoryRawEvent activeDirectoryRawEvent = createEvent();
        activeDirectoryRawEvent.setUserDisplayName(null);
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<AbstractInputDocument>> violations = validator.validate(activeDirectoryRawEvent);
        Assert.assertTrue(CollectionUtils.isEmpty(violations));
    }

    public ActiveDirectoryRawEvent createEvent() {
        ActiveDirectoryRawEvent activeDirectoryRawEvent = new ActiveDirectoryRawEvent(Instant.now(), "eventId",
                "dataSource", "userId", "operationType", null,
                EventResult.SUCCESS, "userName", "userDisplayName", null,
                false, "objectId", "resultCode");
        return activeDirectoryRawEvent;
    }
}
