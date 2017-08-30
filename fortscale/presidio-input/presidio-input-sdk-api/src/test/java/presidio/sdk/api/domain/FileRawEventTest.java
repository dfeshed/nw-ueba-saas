package presidio.sdk.api.domain;

import fortscale.domain.core.EventResult;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Assert;
import org.junit.Test;
import presidio.sdk.api.domain.rawevents.FileRawEvent;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.Instant;
import java.util.Set;

public class FileRawEventTest {

    @Test
    public void testValidRecord() {
        FileRawEvent fileRawEvent = createEvent();
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<FileRawEvent>> violations = validator.validate(fileRawEvent);
        Assert.assertTrue(CollectionUtils.isEmpty(violations));
    }

    public FileRawEvent createEvent() {
        FileRawEvent fileRawEvent = new FileRawEvent(Instant.now(), "eventId", "dataSource",
                "userId", "operationType", null, EventResult.SUCCESS,
                "userName", "userDisplayName", null, "srcFilePath",
                true, "dstFilePath", true, 0L, "resultCode");
        return fileRawEvent;
    }
}
