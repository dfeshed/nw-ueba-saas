package presidio.sdk.api.domain;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.validator.constraints.NotEmpty;
import org.junit.Assert;
import org.junit.Test;
import presidio.sdk.api.domain.rawevents.ProcessRawEvent;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.Instant;
import java.util.Collections;
import java.util.Set;

public class ProcessRawEventTest {

    @Test
    public void testValidRecord() {
        ProcessRawEvent processRawEvent = createRawEvent();

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<ProcessRawEvent>> violations = validator.validate(processRawEvent);
        Assert.assertTrue(CollectionUtils.isEmpty(violations));
    }


    @Test
    public void testRecordNoMachineId() {
        ProcessRawEvent processRawEvent = createRawEvent();
        processRawEvent.setMachineId(null);

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<ProcessRawEvent>> violations = validator.validate(processRawEvent);
        Assert.assertEquals(CollectionUtils.size(violations),1);
    }

    @Test
    public void testRecordNoMachineName() {
        ProcessRawEvent processRawEvent = createRawEvent();
        processRawEvent.setMachineName(null);

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<ProcessRawEvent>> violations = validator.validate(processRawEvent);
        Assert.assertEquals(CollectionUtils.size(violations),1);
    }

    @Test
    public void testRecordNoSrcProcessDirectory() {
        ProcessRawEvent processRawEvent = createRawEvent();
        processRawEvent.setSrcProcessDirectory(null);

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<ProcessRawEvent>> violations = validator.validate(processRawEvent);
        Assert.assertEquals(CollectionUtils.size(violations),1);
    }

    @Test
    public void testRecordNoSrcProcessFilename() {
        ProcessRawEvent processRawEvent = createRawEvent();
        processRawEvent.setSrcProcessFileName(null);

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<ProcessRawEvent>> violations = validator.validate(processRawEvent);
        Assert.assertEquals(CollectionUtils.size(violations),1);
    }

    @Test
    public void testRecordNoDstProcessDirectory() {
        ProcessRawEvent processRawEvent = createRawEvent();
        processRawEvent.setDstProcessDirectory(null);

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<ProcessRawEvent>> violations = validator.validate(processRawEvent);
        Assert.assertEquals(CollectionUtils.size(violations),1);
    }

    @Test
    public void testRecordNoDstProcessFilename() {
        ProcessRawEvent processRawEvent = createRawEvent();
        processRawEvent.setDstProcessFileName(null);

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<ProcessRawEvent>> violations = validator.validate(processRawEvent);
        Assert.assertEquals(CollectionUtils.size(violations),1);
    }

    @Test
    public void testNoUserId() {
        ProcessRawEvent processRawEvent = createRawEvent();
        processRawEvent.setUserId(null);
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<AbstractInputDocument>> violations = validator.validate(processRawEvent);
        Assert.assertEquals(1, violations.size());
        Assert.assertTrue(violations.iterator().next().getConstraintDescriptor().getAnnotation() instanceof NotEmpty);
    }

    @Test
    public void testNoOperationType() {
        ProcessRawEvent processRawEvent = createRawEvent();
        processRawEvent.setOperationType(null);
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<AbstractInputDocument>> violations = validator.validate(processRawEvent);
        Assert.assertEquals(1, violations.size());
        Assert.assertTrue(violations.iterator().next().getConstraintDescriptor().getAnnotation() instanceof NotEmpty);
    }

    @Test
    public void testNoUserName() {
        ProcessRawEvent processRawEvent = createRawEvent();
        processRawEvent.setUserName(null);
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<AbstractInputDocument>> violations = validator.validate(processRawEvent);
        Assert.assertTrue(CollectionUtils.isEmpty(violations));
    }

    @Test
    public void testNoUserDisplayName() {
        ProcessRawEvent processRawEvent = createRawEvent();
        processRawEvent.setUserDisplayName(null);
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<AbstractInputDocument>> violations = validator.validate(processRawEvent);
        Assert.assertTrue(CollectionUtils.isEmpty(violations));
    }

    private ProcessRawEvent createRawEvent() {

        // String machineId, String machineName, String machineOwner, String srcProcessDirectory, String srcProcessFileName, List<String> srcProcessDirectoryGroups, List<String> srcProcessCategories, String srcProcessCertificateIssuer, String dstProcessDirectory, String dstProcessFileName, List<String> dstProcessDirectoryGroups, List<String> dstProcessCategories, String dstProcessCertificateIssuer
        return new ProcessRawEvent(Instant.now(), "eventId", "dataSource", "userId", "operationType", "userName",
                "userDisplayName", null, "machineId","machineName","machineOwner","srcProcessDirectory","srcProcessFileName",Collections.EMPTY_LIST,Collections.EMPTY_LIST,"srcProcessCertificateIssuer","dstProcessDirectory","dstProcessFileName",Collections.EMPTY_LIST,Collections.EMPTY_LIST,"dstProcessCertificateIssuer");

    }
}
