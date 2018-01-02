package fortscale.utils.elasticsearch;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;


/**
 * {@link Pageable} implementation to represent the absence of pagination information.
 *
 * This class was copied from spring data version 2.0
 *
 */
public enum Unpaged  implements Pageable {

    INSTANCE;

    /*
     * (non-Javadoc)
     * @see org.springframework.data.domain.Pageable#previousOrFirst()
     */
    @Override
    public Pageable previousOrFirst() {
        return this;
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.domain.Pageable#next()
     */
    @Override
    public Pageable next() {
        return this;
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.domain.Pageable#hasPrevious()
     */
    @Override
    public boolean hasPrevious() {
        return false;
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.domain.Pageable#getSort()
     */
    @Override
    public Sort getSort() {
        throw new UnsupportedOperationException();
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.domain.Pageable#getPageSize()
     */
    @Override
    public int getPageSize() {
        return 0;
        //throw new UnsupportedOperationException();
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.domain.Pageable#getPageNumber()
     */
    @Override
    public int getPageNumber() {
        throw new UnsupportedOperationException();
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.domain.Pageable#getOffset()
     */
    @Override
    public int getOffset() {
        return 0;
        //throw new UnsupportedOperationException();
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.domain.Pageable#first()
     */
    @Override
    public Pageable first() {
        return this;
    }
}
