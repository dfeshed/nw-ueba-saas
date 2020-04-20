package fortscale.utils.pagination;

import java.util.Iterator;
import java.util.List;

/**
 * List<T> is a page of events.
 * @param <T>
 */
public interface PageIterator<T> extends Iterator<List<T>> {

}
