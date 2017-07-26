package presidio.data.generators.eventsGeneratorTests;

import org.junit.Assert;
import org.junit.Test;
import presidio.data.generators.common.CustomStringGenerator;

public class StringGeneratorTest {

    @Test
    public void CustomStringDefaultTest(){
        CustomStringGenerator generator = new CustomStringGenerator();
        Assert.assertEquals("Default Custom String", generator.getNext());
    }

    @Test
    public void CustomStringTest(){
        CustomStringGenerator generator = new CustomStringGenerator("my string");
        Assert.assertEquals("my string", generator.getNext());
    }
}
