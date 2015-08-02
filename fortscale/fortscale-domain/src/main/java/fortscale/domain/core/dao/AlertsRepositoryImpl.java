package fortscale.domain.core.dao;

import fortscale.domain.core.Alert;
import fortscale.domain.core.AlertStatus;
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
    public Alerts findAlertsByFilters(PageRequest pageRequest, String severityArrayFilter, String statusArrayFilter) {

       //build the query
        Query query = buildQuery(pageRequest, Alert.severityField, Alert.statusField, severityArrayFilter, statusArrayFilter, pageRequest);
        List<Alert> alertsList = mongoTemplate.find(query, Alert.class);
        Alerts alerts = new Alerts();
        alerts.setAlerts(alertsList);
        return alerts;
    }


    @Override
    public Long countAlertsByFilters(PageRequest pageRequest, String severityArrayFilter, String statusArrayFilter) {

        //build the query
        Query query = buildQuery(pageRequest, Alert.severityField, Alert.statusField, severityArrayFilter, statusArrayFilter, pageRequest);
        return mongoTemplate.count(query, Alert.class);
    }


    /**
     * Build a query to be used by mongo API
     * @param pageRequest
     * @param severityFieldName name of the field to access severity property
     * @param statusFieldName name of the field to access status property
     * @param severityArrayFilter comma separated list of severity attributes to include
     * @param statusArrayFilter comma separated list of status attributes to include
     * @param pageable
     * @return
     */
    private Query buildQuery(PageRequest pageRequest, String severityFieldName, String statusFieldName, String severityArrayFilter, String statusArrayFilter, Pageable pageable) {
        List<Alert> result;
        Criteria severityCriteria = new Criteria();
        Criteria statusCriteria = new Criteria();
        Query query = new Query().with( pageRequest.getSort());
        //build severity filter
        if (severityArrayFilter != null) {
            String[] severityFilterVals = severityArrayFilter.split(",");
            List<String> severityList = new ArrayList<>();
            for (String val : severityFilterVals) {
                Severity severity = Severity.getByStringCaseInsensitive(val);
                if (severity != null) {
                    severityList.add(severity.name());
                }
            }
            //If filter includes all severity entries, ignore the filter as it is the same as without filter
            if (severityList.size() != Severity.values().length) {
                severityCriteria = where(severityFieldName).in(severityList);
                query.addCriteria(severityCriteria);
            }
        }
        //build status filter
        if (statusArrayFilter != null) {
            String[] statusFilterVals = statusArrayFilter.split(",");
            List<String> statusList = new ArrayList<>();
            for (String val : statusFilterVals) {
                AlertStatus status = AlertStatus.getByStringCaseInsensitive(val);
                if (status != null) {
                    statusList.add(status.name());
                }
            }
            //If filter includes all status entries, ignore the filter as it is the same as without filter
            if (statusList.size() != AlertStatus.values().length) {
                statusCriteria = where(statusFieldName).in(statusList);
                query.addCriteria(statusCriteria);
            }
        }

//        Query query = new Query(severityCriteria).with( pageRequest.getSort());
        int pageSize = pageRequest.getPageSize();
        int pageNum = pageRequest.getPageNumber();
        query.limit(pageSize);
        query.skip(pageNum * pageSize);
        if (pageable != null) {
            query.with(pageable);
        }
        return query;
    }
}
