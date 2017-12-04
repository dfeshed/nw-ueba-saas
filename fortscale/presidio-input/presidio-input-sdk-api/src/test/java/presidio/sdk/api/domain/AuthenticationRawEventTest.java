package presidio.sdk.api.domain;

import fortscale.domain.core.EventResult;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Assert;
import org.junit.Test;
import presidio.sdk.api.domain.rawevents.AuthenticationRawEvent;
import presidio.sdk.api.validation.FieldsMustHaveDifferentValues;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.Instant;
import java.util.Set;

public class AuthenticationRawEventTest {

    @Test
    public void testValidRecord() {
        AuthenticationRawEvent authenticationRawEvent = createEvent();
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<AuthenticationRawEvent>> violations = validator.validate(authenticationRawEvent);
        Assert.assertTrue(CollectionUtils.isEmpty(violations));
    }

    @Test
    public void testNoSrcMachineId() {
        AuthenticationRawEvent authenticationRawEvent = createEvent();
        authenticationRawEvent.setSrcMachineId(null);
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<AuthenticationRawEvent>> violations = validator.validate(authenticationRawEvent);
        Assert.assertTrue(CollectionUtils.isEmpty(violations));
    }

    @Test
    public void testNoSrcMachineName() {
        AuthenticationRawEvent authenticationRawEvent = createEvent();
        authenticationRawEvent.setSrcMachineName(null);
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<AuthenticationRawEvent>> violations = validator.validate(authenticationRawEvent);
        Assert.assertTrue(CollectionUtils.isEmpty(violations));
    }

    @Test
    public void testNoDstMachineId() {
        AuthenticationRawEvent authenticationRawEvent = createEvent();
        authenticationRawEvent.setDstMachineId(null);
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<AuthenticationRawEvent>> violations = validator.validate(authenticationRawEvent);
        Assert.assertTrue(CollectionUtils.isEmpty(violations));
    }

    @Test
    public void testNoDstMachineName() {
        AuthenticationRawEvent authenticationRawEvent = createEvent();
        authenticationRawEvent.setDstMachineName(null);
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<AuthenticationRawEvent>> violations = validator.validate(authenticationRawEvent);
        Assert.assertTrue(CollectionUtils.isEmpty(violations));
    }

    @Test
    public void testNoDstMachineDomain() {
        AuthenticationRawEvent authenticationRawEvent = createEvent();
        authenticationRawEvent.setDstMachineDomain(null);
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<AuthenticationRawEvent>> violations = validator.validate(authenticationRawEvent);
        Assert.assertTrue(CollectionUtils.isEmpty(violations));
    }

    @Test
    public void testSameSrcAndDstMachineId() {
        AuthenticationRawEvent authenticationRawEvent = createEvent();
        authenticationRawEvent.setSrcMachineId("id");
        authenticationRawEvent.setDstMachineId("id");
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<AuthenticationRawEvent>> violations = validator.validate(authenticationRawEvent);
        Assert.assertTrue(violations.isEmpty());
    }

    @Test
    public void testSrcAndDstMachineIdAreNull() {
        AuthenticationRawEvent authenticationRawEvent = createEvent();
        authenticationRawEvent.setSrcMachineId(null);
        authenticationRawEvent.setDstMachineId(null);
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<AuthenticationRawEvent>> violations = validator.validate(authenticationRawEvent);
        Assert.assertTrue(violations.isEmpty());
    }

    @Test
    public void testSrcAndDstMachineIdAreEmpty() {
        AuthenticationRawEvent authenticationRawEvent = createEvent();
        authenticationRawEvent.setSrcMachineId("");
        authenticationRawEvent.setDstMachineId("");
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<AuthenticationRawEvent>> violations = validator.validate(authenticationRawEvent);
        Assert.assertTrue(violations.isEmpty());
    }

    private AuthenticationRawEvent createEvent() {
        AuthenticationRawEvent authenticationRawEvent = new AuthenticationRawEvent(Instant.now(), "eventId",
                "dataSource", "userId", "operationType", null,
                EventResult.SUCCESS, "userName", "userDisplayName", null,
                "srcMachineId", "srcMachineName", "dstMachineId",
                "dstMachineName", "dstMachineDomain", "resultCode");

        return authenticationRawEvent;
    }
}
