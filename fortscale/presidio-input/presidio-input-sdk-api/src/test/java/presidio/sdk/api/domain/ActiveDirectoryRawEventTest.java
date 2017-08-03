package presidio.sdk.api.domain;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.validator.constraints.NotEmpty;
import org.junit.Assert;
import org.junit.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

public class ActiveDirectoryRawEventTest {

    @Test
    public void testValidRecord() {
        String record = "2017-06-29T08:00:00Z,123456,data_source,ACCOUNT_MANAGEMENT,true,false,objectName1,SUCCESS,testusr1";

        ActiveDirectoryRawEvent activeDirectoryRawEvent = new ActiveDirectoryRawEvent(record.split(","));
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<ActiveDirectoryRawEvent>> violations = validator.validate(activeDirectoryRawEvent);
        Assert.assertTrue(CollectionUtils.isEmpty(violations));
    }

    @Test
    public void testNoEventId() {
        String record = "2017-06-29T08:00:00Z,,data_source,ACCOUNT_MANAGEMENT,true,false,objectName1,SUCCESS,testusr1";

        ActiveDirectoryRawEvent activeDirectoryRawEvent = new ActiveDirectoryRawEvent(record.split(","));
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<ActiveDirectoryRawEvent>> violations = validator.validate(activeDirectoryRawEvent);
        Assert.assertEquals(1, violations.size());
        Assert.assertTrue(violations.iterator().next().getConstraintDescriptor().getAnnotation() instanceof NotEmpty);
    }

    @Test
    public void testNoDataSource() {
        String record = "2017-06-29T08:00:00Z,123,,ACCOUNT_MANAGEMENT,true,false,objectName1,SUCCESS,testusr1";

        ActiveDirectoryRawEvent activeDirectoryRawEvent = new ActiveDirectoryRawEvent(record.split(","));
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<ActiveDirectoryRawEvent>> violations = validator.validate(activeDirectoryRawEvent);
        Assert.assertEquals(1, violations.size());
        Assert.assertTrue(violations.iterator().next().getConstraintDescriptor().getAnnotation() instanceof NotEmpty);
    }

    @Test
    public void testNoObjectName() {
        String record = "2017-06-29T08:00:00Z,123,data source,ACCOUNT_MANAGEMENT,true,false,,SUCCESS,testusr1";

        ActiveDirectoryRawEvent activeDirectoryRawEvent = new ActiveDirectoryRawEvent(record.split(","));
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<ActiveDirectoryRawEvent>> violations = validator.validate(activeDirectoryRawEvent);
        Assert.assertEquals(1, violations.size());
        Assert.assertTrue(violations.iterator().next().getConstraintDescriptor().getAnnotation() instanceof NotEmpty);
    }
}
