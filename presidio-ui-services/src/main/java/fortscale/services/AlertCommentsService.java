package fortscale.services;



import fortscale.domain.core.alert.analystfeedback.AnalystFeedback;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by shays on 06/11/2017.
 */
public interface AlertCommentsService {

    AnalystFeedback addComment(AnalystFeedback analystFeedback);
    void updateComment(AnalystFeedback analystFeedback);
    List<AnalystFeedback> getCommentByAlertId(String alertId);


    AnalystFeedback getCommentById(String commentId);

    Map<String, List<AnalystFeedback>> getCommentByAlertIds(Set<String> alertIds);

    void deleteComment(AnalystFeedback analystFeedback);
}