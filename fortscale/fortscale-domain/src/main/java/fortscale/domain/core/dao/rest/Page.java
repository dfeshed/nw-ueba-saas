package fortscale.domain.core.dao.rest;

import com.splunk.CollectionArgs;

import java.io.Serializable;
import org.springframework.data.domain.Sort;

/**
 * Created by rans on 22/06/15.
 * This bean is used to serialize json for Page statistics in the REST API.
 * E.g., size of page, number of total elements, number of total pages, number of current page
 */
public class Page implements Serializable {
    private static final long serialVersionUID = 3824327949704098079L;

    private Long size;
    private Long totalElements;
    private Long totalPages;
    private Long number;
    private String sortFiled;
    private Sort.Direction sortDirection;

    public Page(Long size, Long totalElements, Long totalPages, Long number, String sortFiled, Sort.Direction sortDirection) {
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.number = number;
        this.sortFiled = sortFiled;
        this.sortDirection = sortDirection;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public Long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(Long totalElements) {
        this.totalElements = totalElements;
    }

    public Long getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Long totalPages) {
        this.totalPages = totalPages;
    }

    public Long getNumber() {
        return number;
    }

    public void setNumber(Long number) {
        this.number = number;
    }

    public String getSortFiled() {
        return sortFiled;
    }

    public void setSortFiled(String sortFiled) {
        this.sortFiled = sortFiled;
    }

    public Sort.Direction getSortDirection() {
        return sortDirection;
    }

    public void setSortDirection(Sort.Direction sortDirection) {
        this.sortDirection = sortDirection;
    }
}
