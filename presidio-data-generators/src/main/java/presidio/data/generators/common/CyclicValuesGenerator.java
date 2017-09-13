package presidio.data.generators.common;

/**
 * Created by cloudera on 6/1/17.
 * This class is one element data provider from a cyclic list of string values
 */
public class CyclicValuesGenerator<T>{
    private T[] values;
    private int currentIdx;

    public CyclicValuesGenerator() {
        this.currentIdx = -1;
    }

    public CyclicValuesGenerator(T value) {
        this.values =(T[])new Object[1];
        this.values[0] = value;
        this.currentIdx = -1;
    }

    public CyclicValuesGenerator(T[] values) {
        this.values = values;
        this.currentIdx = -1;
    }

    public T getNext(){
        currentIdx++;
        if (currentIdx == values.length ) currentIdx = 0;
        return values[currentIdx];
    }

    public void setValuesList(T[] valuesList) {
        this.values = valuesList;
    }

    public T[] getValues() {
        return values;
    }
}
