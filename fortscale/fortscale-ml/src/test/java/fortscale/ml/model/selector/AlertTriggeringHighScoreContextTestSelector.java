package fortscale.ml.model.selector;

import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by baraks on 1/4/2017.
 */
@Configurable(preConstruction = true)
public class AlertTriggeringHighScoreContextTestSelector extends AlertTriggeringHighScoreContextSelector
{
    @Override
    public Set<String> getContexts(Date startTime, Date endTime) {
        HashSet<String> contexts = Sets.newHashSet("user1", "user2", "user3");
        return contexts;
    }

}
