package presidio.sdk.api.domain;

import fortscale.domain.core.EventResult;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.validator.constraints.NotEmpty;
import org.junit.Assert;
import org.junit.Test;
import presidio.sdk.api.domain.rawevents.ActiveDirectoryRawEvent;
import presidio.sdk.api.domain.rawevents.PrintRawEvent;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.Instant;
import java.util.Collections;
import java.util.Set;

public class PrintRawEventTest {

    @Test
    public void testValidRecord() {
        PrintRawEvent printRawEvent = createRawEvent();

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<PrintRawEvent>> violations = validator.validate(printRawEvent);
        Assert.assertTrue(CollectionUtils.isEmpty(violations));
    }


    @Test
    public void testRecordNoSrcMachineId() {
        PrintRawEvent printRawEvent = createRawEvent();
        printRawEvent.setSrcMachineId(null);

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<PrintRawEvent>> violations = validator.validate(printRawEvent);
        Assert.assertTrue(CollectionUtils.isEmpty(violations));
    }

    @Test
    public void testRecordNoSrcMachineName() {
        PrintRawEvent printRawEvent = createRawEvent();
        printRawEvent.setSrcMachineName(null);

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<PrintRawEvent>> violations = validator.validate(printRawEvent);
        Assert.assertTrue(CollectionUtils.isEmpty(violations));
    }

    @Test
    public void testRecordNoPrinterId() {
        PrintRawEvent printRawEvent = createRawEvent();
        printRawEvent.setPrinterId(null);

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<PrintRawEvent>> violations = validator.validate(printRawEvent);
        Assert.assertTrue(CollectionUtils.isEmpty(violations));
    }

    @Test
    public void testRecordNoPrinterName() {
        PrintRawEvent printRawEvent = createRawEvent();
        printRawEvent.setPrinterName(null);

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<PrintRawEvent>> violations = validator.validate(printRawEvent);
        Assert.assertTrue(CollectionUtils.isEmpty(violations));
    }

    @Test
    public void testRecordNoSrcFilePath() {
        PrintRawEvent printRawEvent = createRawEvent();
        printRawEvent.setSrcFilePath(null);

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<PrintRawEvent>> violations = validator.validate(printRawEvent);
        Assert.assertTrue(CollectionUtils.isEmpty(violations));
    }

    @Test
    public void testRecordNoIsSrcDriveShared() {
        PrintRawEvent printRawEvent = createRawEvent();
        printRawEvent.setSrcDriveShared(null);

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<PrintRawEvent>> violations = validator.validate(printRawEvent);
        Assert.assertTrue(CollectionUtils.isEmpty(violations));
    }

    @Test
    public void testRecordNoFileSize() {
        PrintRawEvent printRawEvent = createRawEvent();
        printRawEvent.setFileSize(null);

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<PrintRawEvent>> violations = validator.validate(printRawEvent);
        Assert.assertTrue(CollectionUtils.isEmpty(violations));
    }

    @Test
    public void testRecordNoNumOfPages() {
        PrintRawEvent printRawEvent = createRawEvent();
        printRawEvent.setNumOfPages(null);

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<PrintRawEvent>> violations = validator.validate(printRawEvent);
        Assert.assertTrue(CollectionUtils.isEmpty(violations));
    }

    @Test
    public void testNoUserId() {
        PrintRawEvent printRawEvent = createRawEvent();
        printRawEvent.setUserId(null);
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<AbstractInputDocument>> violations = validator.validate(printRawEvent);
        Assert.assertEquals(1, violations.size());
        Assert.assertTrue(violations.iterator().next().getConstraintDescriptor().getAnnotation() instanceof NotEmpty);
    }

    @Test
    public void testNoOperationType() {
        PrintRawEvent printRawEvent = createRawEvent();
        printRawEvent.setOperationType(null);
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<AbstractInputDocument>> violations = validator.validate(printRawEvent);
        Assert.assertEquals(1, violations.size());
        Assert.assertTrue(violations.iterator().next().getConstraintDescriptor().getAnnotation() instanceof NotEmpty);
    }

    @Test
    public void testNoOperationTypeCategory() {
        PrintRawEvent printRawEvent = createRawEvent();
        printRawEvent.setOperationTypeCategories(null);
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<AbstractInputDocument>> violations = validator.validate(printRawEvent);
        Assert.assertTrue(CollectionUtils.isEmpty(violations));
    }

    @Test
    public void testNoUserName() {
        PrintRawEvent printRawEvent = createRawEvent();
        printRawEvent.setUserName(null);
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<AbstractInputDocument>> violations = validator.validate(printRawEvent);
        Assert.assertTrue(CollectionUtils.isEmpty(violations));
    }

    @Test
    public void testNoUserDisplayName() {
        PrintRawEvent printRawEvent = createRawEvent();
        printRawEvent.setUserDisplayName(null);
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<AbstractInputDocument>> violations = validator.validate(printRawEvent);
        Assert.assertTrue(CollectionUtils.isEmpty(violations));
    }

    private PrintRawEvent createRawEvent() {
        return new PrintRawEvent(Instant.now(), "eventId", "dataSource", "userId",
                "operationType", Collections.EMPTY_LIST, EventResult.SUCCESS, "userName",
                "userDisplayName", null, "resultCode", "srcMachineId",
                "srcMachineName", "printerid", "printerName", "srcFilePath",
                false, 10l, 10l);
    }
}
