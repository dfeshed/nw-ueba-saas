package presidio.data.generators.common;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

import static java.util.Arrays.asList;
public class StringListCyclicGeneratorTest {
    @Test
    public void StringListCyclicTest(){
        StringListCyclicGenerator generator = new StringListCyclicGenerator(asList(new String[] {"my string", "other's string"}));
        Assert.assertEquals("my string", generator.getNext().get(0));
    }

    @Test
    public void FewStringListsTest(){
        List<List<String>> list = asList(
                asList("my string1", "other's string1"),
                asList("my string2", "other's string2", "additional string2" ),
                asList("my string3", "other's string3" ) );

        StringListCyclicGenerator generator = new StringListCyclicGenerator((List<String>[]) list.toArray());

        Assert.assertEquals("other's string1", generator.getNext().get(1));
        Assert.assertEquals("additional string2", generator.getNext().get(2));
        Assert.assertEquals("my string3", generator.getNext().get(0));
    }
}
