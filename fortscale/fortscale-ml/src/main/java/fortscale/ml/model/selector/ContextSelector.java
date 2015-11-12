package fortscale.ml.model.selector;

import java.util.List;


public interface ContextSelector {
    public List<String> getContexts(Long startTime, Long endTime);
}
