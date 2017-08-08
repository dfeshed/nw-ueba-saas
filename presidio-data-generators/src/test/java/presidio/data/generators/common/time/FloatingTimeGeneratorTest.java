package presidio.data.generators.common.time;

import org.junit.Assert;
import org.junit.Test;
import presidio.data.generators.common.GeneratorException;

import java.time.LocalTime;

/**
 * Created by YaronDL on 8/8/2017.
 */
public class FloatingTimeGeneratorTest {
    @Test
    public void TimeGeneratorWithShiftTest() throws GeneratorException {
        TimeGenerator TG =
                new FloatingTimeGenerator(LocalTime.of(0, 0), LocalTime.of(1, 0), 25, 3, 1);

        Assert.assertEquals("00:00:00", TG.getNext().toString().substring(11,19));
        Assert.assertEquals("00:25:00", TG.getNext().toString().substring(11,19));
        Assert.assertEquals("00:50:00", TG.getNext().toString().substring(11,19));
    }
}
