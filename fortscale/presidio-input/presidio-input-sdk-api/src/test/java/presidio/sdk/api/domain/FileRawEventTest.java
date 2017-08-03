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

public class FileRawEventTest {

    @Test
    public void testValidRecord() {
        String record = "2017-05-20T15:50:00Z,123,file,FOLDER_OPENED,username,SUCCESS,srcFilePath,dstFilePath,srcFolderPath,dstFolderPath,123,false,false";

        FileRawEvent fileRawEvent = new FileRawEvent(record.split(","));
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<FileRawEvent>> violations = validator.validate(fileRawEvent);
        Assert.assertTrue(CollectionUtils.isEmpty(violations));
    }

    @Test
    public void testNoEventId() {
        String record = "2017-05-20T15:50:00Z,,file,FOLDER_OPENED,username,SUCCESS,srcFilePath,dstFilePath,srcFolderPath,dstFolderPath,123,false,false";

        FileRawEvent fileRawEvent = new FileRawEvent(record.split(","));
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<FileRawEvent>> violations = validator.validate(fileRawEvent);
        Assert.assertEquals(1, violations.size());
        Assert.assertTrue(violations.iterator().next().getConstraintDescriptor().getAnnotation() instanceof NotEmpty);
    }

    @Test
    public void testNoDataSource() {
        String record = "2017-05-20T15:50:00Z,123,,FOLDER_OPENED,username,SUCCESS,srcFilePath,dstFilePath,srcFolderPath,dstFolderPath,123,false,false";

        FileRawEvent fileRawEvent = new FileRawEvent(record.split(","));
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<FileRawEvent>> violations = validator.validate(fileRawEvent);
        Assert.assertEquals(1, violations.size());
        Assert.assertTrue(violations.iterator().next().getConstraintDescriptor().getAnnotation() instanceof NotEmpty);
    }

    @Test
    public void testNoUsername() {
        String record = "2017-05-20T15:50:00Z,123,file,FOLDER_OPENED,,SUCCESS,srcFilePath,dstFilePath,srcFolderPath,dstFolderPath,123,false,false";

        FileRawEvent fileRawEvent = new FileRawEvent(record.split(","));
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<FileRawEvent>> violations = validator.validate(fileRawEvent);
        Assert.assertEquals(1, violations.size());
        Assert.assertTrue(violations.iterator().next().getConstraintDescriptor().getAnnotation() instanceof NotEmpty);
    }
}
