package presidio.sdk.api.validation;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Assert;
import org.junit.Test;
import presidio.sdk.api.domain.rawevents.AuthenticationRawEvent;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.Instant;
import java.util.Set;

/**
 * Created by efratn on 03/12/2017.
 */
public class AuthenticationEventValidationsTest {


    @Test
    public void authRawEventValidationTest_srcAndDstCanBeEquals() {
        AuthenticationRawEvent authRawEvent = new AuthenticationRawEvent();
        authRawEvent.setDstMachineId("test");
        authRawEvent.setSrcMachineId("test");
        authRawEvent.setEventId("eventId");
        authRawEvent.setDataSource("dataSource");
        authRawEvent.setUserId("userId");
        authRawEvent.setOperationType("opType");
        authRawEvent.setDateTime(Instant.now());


        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<AuthenticationRawEvent>> violations = validator.validate(authRawEvent);
        Assert.assertTrue(CollectionUtils.isEmpty(violations));
    }

    @Test
    public void authRawEventValidationTest_srcAndDstWithDifferentValues() {
        AuthenticationRawEvent authRawEvent = new AuthenticationRawEvent();
        authRawEvent.setDstMachineId("test1");
        authRawEvent.setSrcMachineId("test2");
        authRawEvent.setEventId("eventId");
        authRawEvent.setDataSource("dataSource");
        authRawEvent.setUserId("userId");
        authRawEvent.setOperationType("opType");
        authRawEvent.setDateTime(Instant.now());

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<AuthenticationRawEvent>> violations = validator.validate(authRawEvent);
        Assert.assertTrue(CollectionUtils.isEmpty(violations));
    }

    @Test
    public void authRawEventValidationTest_srcAndDstAreNull() {
        AuthenticationRawEvent authRawEvent = new AuthenticationRawEvent();
        authRawEvent.setDstMachineId(null);
        authRawEvent.setSrcMachineId(null);
        authRawEvent.setEventId("eventId");
        authRawEvent.setDataSource("dataSource");
        authRawEvent.setUserId("userId");
        authRawEvent.setOperationType("opType");
        authRawEvent.setDateTime(Instant.now());

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<AuthenticationRawEvent>> violations = validator.validate(authRawEvent);
        Assert.assertTrue(CollectionUtils.isEmpty(violations));
    }

    @Test
    public void authRawEventValidationTest_srcAndDstAreEmpty() {
        AuthenticationRawEvent authRawEvent = new AuthenticationRawEvent();
        authRawEvent.setDstMachineId("");
        authRawEvent.setSrcMachineId("");
        authRawEvent.setEventId("eventId");
        authRawEvent.setDataSource("dataSource");
        authRawEvent.setUserId("userId");
        authRawEvent.setOperationType("opType");
        authRawEvent.setDateTime(Instant.now());

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<AuthenticationRawEvent>> violations = validator.validate(authRawEvent);
        Assert.assertTrue(CollectionUtils.isEmpty(violations));
    }
}
