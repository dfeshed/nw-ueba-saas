package fortscale.services.ipresolving.cache;

public interface ResolvingCache<T> {

    T get(String ip);
    void put(String ip, T event);
}
