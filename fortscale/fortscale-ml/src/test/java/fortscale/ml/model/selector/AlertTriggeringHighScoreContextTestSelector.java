package fortscale.ml.model.selector;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by baraks on 1/4/2017.
 */
public class AlertTriggeringHighScoreContextTestSelector extends AlertTriggeringHighScoreContextSelector
{
    @Override
    public List<String> getContexts(Date startTime, Date endTime) {
        return Arrays.asList("user1","user2","user3");
    }

}
