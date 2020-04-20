package presidio.sdk.api.domain;

import fortscale.domain.core.ioc.Level;
import fortscale.domain.core.ioc.Tactic;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.validator.constraints.NotEmpty;
import org.junit.Assert;
import org.junit.Test;
import presidio.sdk.api.domain.rawevents.IocRawEvent;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.Instant;
import java.util.Set;

public class IocRawEventTest {

    @Test
    public void testValidRecord() {
        IocRawEvent iocRawEvent = createEvent();
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<IocRawEvent>> violations = validator.validate(iocRawEvent);
        Assert.assertTrue(CollectionUtils.isEmpty(violations));
    }

    @Test
    public void testRecordNoIocName() {
        IocRawEvent iocRawEvent = createEvent();
        iocRawEvent.setName(null);

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<IocRawEvent>> violations = validator.validate(iocRawEvent);
        Assert.assertEquals(CollectionUtils.size(violations),1);
    }


    @Test
    public void testRecordNoMachineId() {
        IocRawEvent iocRawEvent = createEvent();
        iocRawEvent.setMachineId(null);

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<IocRawEvent>> violations = validator.validate(iocRawEvent);
        Assert.assertEquals(CollectionUtils.size(violations),1);
    }

    @Test
    public void testRecordNoMachineName() {
        IocRawEvent iocRawEvent = createEvent();
        iocRawEvent.setMachineName(null);

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<IocRawEvent>> violations = validator.validate(iocRawEvent);
        Assert.assertEquals(CollectionUtils.size(violations),1);
    }

    @Test
    public void testNoUserId() {
        IocRawEvent iocRawEvent = createEvent();
        iocRawEvent.setUserId(null);
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<AbstractInputDocument>> violations = validator.validate(iocRawEvent);
        Assert.assertEquals(1, violations.size());
        Assert.assertTrue(violations.iterator().next().getConstraintDescriptor().getAnnotation() instanceof NotEmpty);
    }

    @Test
    public void testNoUserName() {
        IocRawEvent iocRawEvent = createEvent();
        iocRawEvent.setUserName(null);
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<AbstractInputDocument>> violations = validator.validate(iocRawEvent);
        Assert.assertTrue(CollectionUtils.isEmpty(violations));
    }


    public IocRawEvent createEvent() {
        IocRawEvent iocRawEvent = new IocRawEvent(Instant.now(), "eventId", "dataSource",
                "userId", "userName", "userDisplayName", null, "ioc name",
                Tactic.PERSISTENCE, Level.CRITICAL, "machine_id", "machine_name");
        return iocRawEvent;
    }
}
