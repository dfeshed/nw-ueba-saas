package fortscale.ml.model.pagination;

import fortscale.ml.model.ModelConf;
import fortscale.ml.model.store.ModelDAO;
import fortscale.ml.model.store.ModelReader;
import fortscale.utils.pagination.PageIterator;

import java.time.Instant;
import java.util.List;
import java.util.Set;

/**
 * Implements PageIterator.
 * PriorModelPageIterator use ModelReader to get list of model records.
 * By using num of items to skip and num of items to read, reader get the records for current iteration.
 */
public class PriorModelPageIterator implements PageIterator<ModelDAO> {

    private ModelConf modelConf;
    private Instant eventEpochTime;
    private Set<String> contextIds;
    private int pageSize;
    private int totalAmountOfPages;
    private ModelReader reader;
    private int currentPage;

    /**
     *
     * @param modelConf modelConf
     * @param eventEpochTime eventEpochTime
     * @param contextIds contextIds
     * @param pageSize pageSize
     * @param totalAmountOfPages totalAmountOfPages
     * @param reader reader
     */
    public PriorModelPageIterator(ModelConf modelConf, Instant eventEpochTime, Set<String> contextIds, int pageSize, int totalAmountOfPages, ModelReader reader) {
        this.currentPage = 0;
        this.modelConf = modelConf;
        this.eventEpochTime = eventEpochTime;
        this.contextIds = contextIds;
        this.pageSize = pageSize;
        this.totalAmountOfPages = totalAmountOfPages;
        this.reader = reader;
    }

    @Override
    public boolean hasNext() {
        return this.currentPage < this.totalAmountOfPages;
    }


    @Override
    public List<ModelDAO> next() {
        int numOfItemsToSkip = this.currentPage * this.pageSize;
        this.currentPage++;
        return this.reader.readRecords(modelConf, eventEpochTime, this.contextIds, numOfItemsToSkip, this.pageSize);
    }

}
