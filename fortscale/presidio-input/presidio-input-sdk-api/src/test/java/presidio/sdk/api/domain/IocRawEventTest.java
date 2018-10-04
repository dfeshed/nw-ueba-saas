package presidio.sdk.api.domain;

import fortscale.domain.core.EventResult;
import fortscale.domain.core.Level;
import fortscale.domain.core.Tactic;
import org.apache.commons.collections.CollectionUtils;
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

    public IocRawEvent createEvent() {
        IocRawEvent iocRawEvent = new IocRawEvent(Instant.now(), "eventId", "dataSource",
                "userId", "operationType", null, EventResult.SUCCESS,
                "userName", "userDisplayName", null, "ioc name",
                Tactic.PERSISTENCE, Level.CRITICAL, "machine_id", "machine_name", "machineOwner", "resultCode");
        return iocRawEvent;
    }
}
