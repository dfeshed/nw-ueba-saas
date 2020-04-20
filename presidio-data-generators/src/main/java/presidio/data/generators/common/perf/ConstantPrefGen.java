package presidio.data.generators.common.perf;

import presidio.data.generators.common.list.content.Locations;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;

public abstract class ConstantPrefGen<T> extends UniformBasedPerfGen<T> {

    protected abstract List<T> getConstantCollection();

    public ConstantPrefGen(int uniqueId, int amount) {
        super(uniqueId, amount);
    }

    @Override
    protected Function<Integer, T> getMappingFunc() {
        return i -> getConstantCollection().get(ThreadLocalRandom.current().nextInt(Locations.LOCATIONS.size()));
    }

}
