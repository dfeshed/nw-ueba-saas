package fortscale.ml.model.selector;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.util.Assert;

/**
 * Created by barak_schuster on 24/08/2017.
 */
public class AccumulatedSmartContextSelectorConf implements IContextSelectorConf {

    public static final String ACCUMULATED_SMART_CONTEXT_SELECTOR_FACTORY_NAME = "accumulated_smart_context_selector";
    private final String smartRecordConfName;

    @JsonCreator
    public AccumulatedSmartContextSelectorConf(
            @JsonProperty("smartRecordConfName") String smartRecordConfName) {
        Assert.hasText(smartRecordConfName, "smartRecordConfName must be non empty");
        this.smartRecordConfName = smartRecordConfName;
    }

    @Override
    public String getFactoryName() {
        return ACCUMULATED_SMART_CONTEXT_SELECTOR_FACTORY_NAME;
    }

    public String getSmartRecordConfName() {
        return smartRecordConfName;
    }
}
