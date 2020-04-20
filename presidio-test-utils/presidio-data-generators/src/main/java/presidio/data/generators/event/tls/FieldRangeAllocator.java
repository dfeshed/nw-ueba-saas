package presidio.data.generators.event.tls;

import presidio.data.generators.FixedValueGenerator;
import presidio.data.generators.NullGenerator;
import presidio.data.generators.common.list.RangeGenerator;

public abstract class FieldRangeAllocator<T> {

    private RangeGenerator<T> generator;

    abstract public RangeGenerator<T> nextRangeGenCyclic(int range);

    abstract public RangeGenerator<T> nextRangeRandom(int range);

    public RangeGenerator<T> setConstantValueGen(T value) {
        FixedValueGenerator<T> generator = new FixedValueGenerator<>(value);
        setGenerator(generator);
        return generator;
    }

    public RangeGenerator<T> deleteField() {
        RangeGenerator<T> generator = new NullGenerator<>();
        setGenerator(generator);
        return generator;
    }

    public RangeGenerator<T> getGenerator() {
        return generator;
    }

    public void setGenerator(RangeGenerator<T> generator) {
        this.generator = generator;
    }

    public void reset() {
        generator.reset();
    }
}
