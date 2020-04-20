package fortscale.utils.data;

public class Countable<E> implements Comparable<Countable<E>> {
    private E element;
    private int count;

    public Countable(E element, int count) {
        if (count < 0) throw new IllegalArgumentException("count must be greater than or equal to zero.");
        this.element = element;
        this.count = count;
    }

    public E getElement() {
        return element;
    }

    public void setElement(E element) {
        this.element = element;
    }

    public int getCount() {
        return count;
    }

    public void incrementCount() {
        ++count;
    }

    @Override
    public int compareTo(Countable<E> countable) {
        if (countable == null) throw new NullPointerException("countable cannot be null.");
        return count - countable.count;
    }
}
