package presidio.data.generators.common.perf;

import presidio.data.generators.IBaseGenerator;

import java.util.function.Function;
import java.util.function.Supplier;

public abstract class DistributionBasedGen<T, U> implements IBaseGenerator<T> {

    private final Function<T, T> TRANSFORMATION_FUNC;
    private final Function<U, T> MAPPING_FUNC;
    private final Supplier<U> SAMPLE;
    protected final int UNIQUE_ID;

    public DistributionBasedGen(int uniqueId) {
        TRANSFORMATION_FUNC = getTransformationFunc();
        MAPPING_FUNC = getMappingFunc();
        SAMPLE = getSample();
        UNIQUE_ID = uniqueId;
    }

    protected abstract Function<U, T> getMappingFunc();
    protected abstract Function<T, T> getTransformationFunc();
    protected abstract Supplier<U> getSample();


    @Override
    public T getNext() {
        return TRANSFORMATION_FUNC.compose(MAPPING_FUNC).apply(SAMPLE.get());
    }


    public int getUniqueId() {
        return UNIQUE_ID;
    }
}

