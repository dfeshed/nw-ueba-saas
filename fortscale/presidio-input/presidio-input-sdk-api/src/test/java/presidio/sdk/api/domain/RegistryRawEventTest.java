package presidio.sdk.api.domain;

import fortscale.domain.core.EventResult;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Assert;
import org.junit.Test;
import presidio.sdk.api.domain.rawevents.RegistryRawEvent;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.Instant;
import java.util.Collections;
import java.util.Set;

public class RegistryRawEventTest {

    @Test
    public void testValidRecord() {
        RegistryRawEvent processRawEvent = createRawEvent();

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<RegistryRawEvent>> violations = validator.validate(processRawEvent);
        Assert.assertTrue(CollectionUtils.isEmpty(violations));
    }


    @Test
    public void testRecordNoMachineId() {
        RegistryRawEvent processRawEvent = createRawEvent();
        processRawEvent.setMachineId(null);

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<RegistryRawEvent>> violations = validator.validate(processRawEvent);
        Assert.assertEquals(CollectionUtils.size(violations),1);
    }

    @Test
    public void testRecordNoMachineName() {
        RegistryRawEvent processRawEvent = createRawEvent();
        processRawEvent.setMachineName(null);

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<RegistryRawEvent>> violations = validator.validate(processRawEvent);
        Assert.assertEquals(CollectionUtils.size(violations),1);
    }

    @Test
    public void testRecordNoProcessDirectory() {
        RegistryRawEvent processRawEvent = createRawEvent();
        processRawEvent.setProcessDirectory(null);

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<RegistryRawEvent>> violations = validator.validate(processRawEvent);
        Assert.assertEquals(CollectionUtils.size(violations),1);
    }

    @Test
    public void testRecordNoProcessFilename() {
        RegistryRawEvent processRawEvent = createRawEvent();
        processRawEvent.setProcessFileName(null);

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<RegistryRawEvent>> violations = validator.validate(processRawEvent);
        Assert.assertEquals(CollectionUtils.size(violations),1);
    }

    @Test
    public void testRecordNoRegistryKey() {
        RegistryRawEvent processRawEvent = createRawEvent();
        processRawEvent.setRegistryKey(null);

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<RegistryRawEvent>> violations = validator.validate(processRawEvent);
        Assert.assertEquals(CollectionUtils.size(violations),1);
    }



    @Test
    public void testRecordNoRegistryValueName() {
        RegistryRawEvent processRawEvent = createRawEvent();
        processRawEvent.setRegistryValueName(null);

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<RegistryRawEvent>> violations = validator.validate(processRawEvent);
        Assert.assertEquals(CollectionUtils.size(violations),1);
    }

    private RegistryRawEvent createRawEvent() {

        return new RegistryRawEvent(Instant.now(), "eventId", "dataSource", "userId",
                "operationType", Collections.EMPTY_LIST, EventResult.SUCCESS, "userName",
                "userDisplayName", null, "resultCode", "machineId","machineName","machineOwner","processDirectory","processFileName",Collections.EMPTY_LIST,Collections.EMPTY_LIST,"processCertificateIssuer","RUN_KEY","registryKey","registryValueName");

    }
}

