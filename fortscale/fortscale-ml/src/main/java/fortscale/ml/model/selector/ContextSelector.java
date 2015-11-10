package fortscale.ml.model.selector;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;


public interface ContextSelector {
    public List<String> getContexts();
}
