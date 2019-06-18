package presidio.data.generators;

public class FixedValueGenerator<T> implements IBaseGenerator<T> {

    private T object;

    public FixedValueGenerator(T object) {
        this.object = object;
    }


    @Override
    public T getNext() {
       return object;
    }

}
