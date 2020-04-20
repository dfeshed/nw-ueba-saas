package presidio.webapp.convertors;

public interface RestPersistencyConvertor<T,C> {

    T convertFromRestToPersistent(C restObject);

    C convertFromPersistentToRest(T persistantObject);
}
