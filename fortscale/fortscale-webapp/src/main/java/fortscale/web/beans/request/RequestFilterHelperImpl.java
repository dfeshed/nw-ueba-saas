package fortscale.web.beans.request;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

/**
 * Created by shays on 04/05/2016.
 * T - the type of the filter
 * R - the type of the DTO
 *
 * Helper for AlertFilter.
 * Abstract common class to helps in common task of DTO filters like parsing of sort attributes, page request attribute and much more
 */


public abstract class RequestFilterHelperImpl<T extends  RestFilter,R> {

    //Constants
    protected static final int DEFAULT_PAGE_SIZE = 200;

    //Abstract methods
    protected abstract Sort getSort(T filterDTO);
    public abstract boolean isFilterEmpty(T filterDTO);


    //Common methods
    public PageRequest getPageRequest(T filterDTO){
        Sort sortByTSDesc = getSort(filterDTO);
        //if pageForMongo is not set, get first pageForMongo
        //Mongo pages start with 0. While on the API the first page is 1.
        int pageForMongo;
        if (filterDTO.getFromPage() == null) {
            pageForMongo = 0;
        } else {
            pageForMongo = filterDTO.getFromPage() -1;
        }
        if (filterDTO.getSize() == null){
            filterDTO.setSize(DEFAULT_PAGE_SIZE);
        }



        PageRequest pageRequest = new PageRequest(pageForMongo, filterDTO.getSize(), sortByTSDesc);
        return  pageRequest;
    }

    public int getOffset(T filterDTO){
        PageRequest pageRequest = getPageRequest(filterDTO);
        return pageRequest.getPageNumber() * pageRequest.getPageSize();
    }


}
