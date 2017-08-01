package presidio.sdk.api.domain;

import fortscale.domain.core.AuthenticationRawEvent;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.validator.constraints.NotEmpty;
import org.junit.Assert;
import org.junit.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

public class AuthenticationRawEventTest {

    @Test
    public void testValidRecord() {
        String record = "2017-05-20T15:50:00Z,123,file,dd,false,machine,machine,username,SUCCESS,1";

        AuthenticationRawEvent authenticationRawEvent = new AuthenticationRawEvent(record.split(","));
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<AuthenticationRawEvent>> violations = validator.validate(authenticationRawEvent);
        Assert.assertTrue(CollectionUtils.isEmpty(violations));
    }

    @Test
    public void testNoEventId() {
        String record = "2017-05-20T15:50:00Z,,file,dd,false,machine,machine,username,SUCCESS,1";

        AuthenticationRawEvent authenticationRawEvent = new AuthenticationRawEvent(record.split(","));
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<AuthenticationRawEvent>> violations = validator.validate(authenticationRawEvent);
        Assert.assertEquals(1, violations.size());
        Assert.assertTrue(violations.iterator().next().getConstraintDescriptor().getAnnotation() instanceof NotEmpty);
    }

    @Test
    public void testNoDataSource() {
        String record = "2017-05-20T15:50:00Z,123,,dd,false,machine,machine,username,SUCCESS,1";

        AuthenticationRawEvent authenticationRawEvent = new AuthenticationRawEvent(record.split(","));
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<AuthenticationRawEvent>> violations = validator.validate(authenticationRawEvent);
        Assert.assertEquals(1, violations.size());
        Assert.assertTrue(violations.iterator().next().getConstraintDescriptor().getAnnotation() instanceof NotEmpty);
    }

    @Test
    public void testNoDstMachine() {
        String record = "2017-05-20T15:50:00Z,123,ff,dd,false,,machine,username,SUCCESS,1";

        AuthenticationRawEvent authenticationRawEvent = new AuthenticationRawEvent(record.split(","));
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<AuthenticationRawEvent>> violations = validator.validate(authenticationRawEvent);
        Assert.assertEquals(1, violations.size());
        Assert.assertTrue(violations.iterator().next().getConstraintDescriptor().getAnnotation() instanceof NotEmpty);
    }

    @Test
    public void testNoSrcMachine() {
        String record = "2017-05-20T15:50:00Z,123,ff,dd,false,machine,,username,SUCCESS,1";

        AuthenticationRawEvent authenticationRawEvent = new AuthenticationRawEvent(record.split(","));
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<AuthenticationRawEvent>> violations = validator.validate(authenticationRawEvent);
        Assert.assertEquals(1, violations.size());
        Assert.assertTrue(violations.iterator().next().getConstraintDescriptor().getAnnotation() instanceof NotEmpty);
    }

    @Test
    public void testNoUsername() {
        String record = "2017-05-20T15:50:00Z,123,ff,dd,false,machine,machine,,SUCCESS,1";

        AuthenticationRawEvent authenticationRawEvent = new AuthenticationRawEvent(record.split(","));
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<AuthenticationRawEvent>> violations = validator.validate(authenticationRawEvent);
        Assert.assertEquals(1, violations.size());
        Assert.assertTrue(violations.iterator().next().getConstraintDescriptor().getAnnotation() instanceof NotEmpty);
    }
}
