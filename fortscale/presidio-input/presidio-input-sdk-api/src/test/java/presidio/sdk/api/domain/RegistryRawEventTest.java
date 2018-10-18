package presidio.sdk.api.domain;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.validator.constraints.NotEmpty;
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

    @Test
    public void testNoUserId() {
        RegistryRawEvent authenticationRawEvent = createRawEvent();
        authenticationRawEvent.setUserId(null);
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<AbstractInputDocument>> violations = validator.validate(authenticationRawEvent);
        Assert.assertEquals(1, violations.size());
        Assert.assertTrue(violations.iterator().next().getConstraintDescriptor().getAnnotation() instanceof NotEmpty);
    }

    @Test
    public void testNoOperationType() {
        RegistryRawEvent registryRawEvent = createRawEvent();
        registryRawEvent.setOperationType(null);
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<AbstractInputDocument>> violations = validator.validate(registryRawEvent);
        Assert.assertEquals(1, violations.size());
        Assert.assertTrue(violations.iterator().next().getConstraintDescriptor().getAnnotation() instanceof NotEmpty);
    }

    @Test
    public void testNoUserName() {
        RegistryRawEvent registryRawEvent = createRawEvent();
        registryRawEvent.setUserName(null);
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<AbstractInputDocument>> violations = validator.validate(registryRawEvent);
        Assert.assertTrue(CollectionUtils.isEmpty(violations));
    }

    @Test
    public void testNoUserDisplayName() {
        RegistryRawEvent registryRawEvent = createRawEvent();
        registryRawEvent.setUserDisplayName(null);
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<AbstractInputDocument>> violations = validator.validate(registryRawEvent);
        Assert.assertTrue(CollectionUtils.isEmpty(violations));
    }

    private RegistryRawEvent createRawEvent() {
            return new RegistryRawEvent(Instant.now(), "eventId", "dataSource", "userId", "operationType", "userName",
                "userDisplayName", null, "machineId","machineName","machineOwner","processDirectory","processFileName",Collections.EMPTY_LIST,Collections.EMPTY_LIST,"processCertificateIssuer","RUN_KEY","registryKey","registryValueName");

    }
}

