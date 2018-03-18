package fortscale.services;

import fortscale.domain.core.alert.analystfeedback.AnalystFeedback;
import fortscale.domain.core.dao.AlertCommentsRepository;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by shays on 06/11/2017.
 */
@Service("alertCommentService")
public class AlertCommentsServiceImpl implements AlertCommentsService {

    @Autowired
    private  AlertCommentsRepository alertCommentsRepository;


    @Override
    public AnalystFeedback addComment(AnalystFeedback analystFeedback) {
        return alertCommentsRepository.save(analystFeedback);
    }

    @Override
    public void updateComment(AnalystFeedback analystFeedback) {
        alertCommentsRepository.save(analystFeedback);
    }

    @Override
    public List<AnalystFeedback> getCommentByAlertId(String alertId) {
        return  alertCommentsRepository.findByAlertId(alertId);
    }

    @Override
    public AnalystFeedback getCommentById(String commentId){
        return alertCommentsRepository.findOne(commentId);
    }

    @Override
    public Map<String, List<AnalystFeedback>> getCommentByAlertIds(Set<String> alertIds){
        Map<String, List<AnalystFeedback>> response = new HashMap<>();
        List<AnalystFeedback> analystFeedbacks = alertCommentsRepository.findByAlertIdIn(alertIds);
        if (CollectionUtils.isEmpty(analystFeedbacks)){
            return  null;
        }
        analystFeedbacks.forEach(comment -> {
            List<AnalystFeedback> commentsOfalerts = response.get(comment.getAlertId());
            if (commentsOfalerts == null){
                commentsOfalerts = new ArrayList<>();
                response.put(comment.getAlertId(),commentsOfalerts);
            }
            commentsOfalerts.add(comment);
        });

        return response;

    }


    public void deleteComment(AnalystFeedback analystFeedback){
        alertCommentsRepository.delete(analystFeedback.getId());
    }
}