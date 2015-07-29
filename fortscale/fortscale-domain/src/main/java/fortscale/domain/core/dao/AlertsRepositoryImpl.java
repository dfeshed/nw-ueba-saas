package fortscale.domain.core.dao;

import fortscale.domain.core.Alert;
import fortscale.domain.core.Severity;
import fortscale.domain.core.dao.rest.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import parquet.org.slf4j.Logger;
import parquet.org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 * Created by rans on 21/06/15.
 */
public class AlertsRepositoryImpl implements AlertsRepositoryCustom {
    private static Logger logger = LoggerFactory.getLogger(AlertsRepositoryImpl.class);
    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * returns all alerts in the collection in a json object represented by @Alerts
     * @param pageRequest
     * @return
     */
    @Override
    public Alerts findAll(PageRequest pageRequest) {
        Query query = new Query( ).with( pageRequest.getSort() );
        int pageSize = pageRequest.getPageSize();
        int pageNum = pageRequest.getPageNumber();
        query.limit(pageSize);
        query.skip(pageNum * pageSize);
        List<Alert> alertsList = mongoTemplate.find(query, Alert.class);
        Alerts alerts = new Alerts();
        alerts.setAlerts(alertsList);
        return alerts;
    }

    @Override
    public Long count(PageRequest pageRequest){
        Query query = new Query( ).with( pageRequest.getSort() );
        query.limit(pageRequest.getPageSize());
        query.skip(pageRequest.getPageNumber());
        Long count = mongoTemplate.count(query, Alert.class);
        return count;
    }

    /**
     * Adds alert object to the Alerts collection
     * @param alert
     */
    @Override
    public void add(Alert alert) {
        mongoTemplate.insert(alert);
    }

    /**
     * gets a single alert according to id
     * @param id
     * @return
     */
    @Override
    public Alert getAlertById(String id){
      return mongoTemplate.findById(id,Alert.class);
    }


    @Override
    public Alerts findAlertsByFilters(PageRequest pageRequest, String severityArrayFilter) {
        String[] filterVals = severityArrayFilter.split(",");
        List<String> severityList = new ArrayList<>();
        for (String val : filterVals){
            Severity severity = Severity.getByStringCaseInsensitive(val);
            if (severity != null){
                severityList.add(severity.name());
            }
        }
        //If filter includes all entries, remove the filter as it is the same as without filter
        if (severityList.size() == Severity.values().length){
            severityList = null;
        }
        List<Alert> alertsList = findByField(pageRequest, Alert.severityField, severityList, pageRequest);
        Alerts alerts = new Alerts();
        alerts.setAlerts(alertsList);
        return alerts;
    }

    /**
     *
     * @param pageRequest
     * @param severityFieldName
     * @param severityList
     * @param pageable
     * @return
     */
    private List<Alert> findByField(PageRequest pageRequest, String severityFieldName, List<String> severityList, Pageable pageable) {
        List<Alert> result;

        Criteria criteria = new Criteria();
        if (severityList != null) {
            criteria = where(severityFieldName).in(severityList);
        }
        Query query = new Query(criteria).with( pageRequest.getSort());
        int pageSize = pageRequest.getPageSize();
        int pageNum = pageRequest.getPageNumber();
        query.limit(pageSize);
        query.skip(pageNum * pageSize);
        if (pageable != null) {
            query.with(pageable);
        }
        result = mongoTemplate.find(query, Alert.class);
        return result;
    }
}
