package presidio.data.generators.common.perf.generators;

import org.testng.Assert;
import presidio.data.generators.IBaseGenerator;
import presidio.data.generators.common.perf.lists.LimitedListMapper;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class LimitedListPerfGenerator<T> implements IBaseGenerator<T> {

    private List<T> pickedValues;
    private int next;

    public LimitedListPerfGenerator(long numOfValues, LimitedListMapper<T> valuesMapper) {
        Assert.assertTrue(numOfValues <= valuesMapper.size());

        Random random = new Random();
        next = 0;
        pickedValues = random.ints(numOfValues, 0, valuesMapper.size())
                .mapToObj(valuesMapper::indexToValue)
                .collect(Collectors.toList());
    }

    @Override
    public T getNext() {
        if (next >= pickedValues.size()) {
            next = 0;
        }
        return pickedValues.get(next++);
    }
}
