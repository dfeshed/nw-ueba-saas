package presidio.data.generators.common;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by barak_schuster on 9/4/17.
 */
public class FixedMapGeneratorTest {
    @Test
    public void testFixedMapGenerator() {

        HashMap<Integer,Double> fixedMap1 = new HashMap<>();
        fixedMap1.put(1,2.3D);
        fixedMap1.put(3,3.3D);
        HashMap<Integer,Double> fixedMap2 = new HashMap<>();
        fixedMap2.put(12,222.3D);
        fixedMap2.put(32,322.3D);
        List<Map<Integer, Double>> fixedMaps = Stream.of(fixedMap1, fixedMap2).collect(Collectors.toList());

        FixedMapGenerator<Integer,Double> fixedMapGenerator = new FixedMapGenerator<Integer,Double>(fixedMaps);

        Assert.assertEquals(fixedMap1, fixedMapGenerator.getNext());
        Assert.assertEquals(fixedMap2, fixedMapGenerator.getNext());
        Assert.assertEquals(fixedMap1, fixedMapGenerator.getNext());

    }
}