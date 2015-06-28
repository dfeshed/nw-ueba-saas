package fortscale.domain.core.dao;

import fortscale.domain.core.dao.rest.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rans on 21/06/15.
 */
public class AlertsRepositoryImpl implements AlertsRepositoryCustom {
    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * returns all alerts in the collection in a json object represented by @Alerts
     * @param pageRequest
     * @param httpRequest
     * @return
     */
    @Override
    public Alerts findAll(PageRequest pageRequest, HttpServletRequest httpRequest) {
        Query query = new Query( ).with( pageRequest.getSort() );
        int pageSize = pageRequest.getPageSize();
        int pageNum = pageRequest.getPageNumber();
        query.limit(pageSize);
        query.skip(pageNum * pageSize);
        List<Alert> alertsList = mongoTemplate.find(query, Alert.class);
        Alerts alerts = new Alerts();
        alerts.set_embedded(new Embedded<List<Alert>>(alertsList));
        LinkUrl linkUrlSelf = new LinkUrl("self", httpRequest.getRequestURI());
        List<LinkUrl> linkUrls = new ArrayList<>();
        linkUrls.add(linkUrlSelf);
        Links links = new Links(linkUrls);
        alerts.set_links(links);
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

}
