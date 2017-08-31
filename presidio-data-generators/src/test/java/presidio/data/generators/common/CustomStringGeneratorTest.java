package presidio.data.generators.common;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by YaronDL on 8/8/2017.
 */
public class CustomStringGeneratorTest {
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

    @Test
    public void PseudoRandomStringTest(){
        RandomStringGenerator generator = new RandomStringGenerator(15);
        System.out.println(generator.getNext());
        System.out.println(generator.getNext());
        System.out.println(generator.getNext());

        //Assert.assertEquals("my string", generator.getNext());
    }
}
