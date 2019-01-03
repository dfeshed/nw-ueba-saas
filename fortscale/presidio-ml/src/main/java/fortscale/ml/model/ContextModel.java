package fortscale.ml.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@JsonAutoDetect(
        fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE,
        setterVisibility = Visibility.NONE, isGetterVisibility = Visibility.NONE)
public class ContextModel implements Model {
    private long numOfContexts;

    public ContextModel(long numOfContexts) {
        this.numOfContexts = numOfContexts;
    }

    public long getNumOfContexts() {
        return numOfContexts;
    }

    @Override
    public long getNumOfSamples() {
        return numOfContexts;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ContextModel)) return false;
        ContextModel contextModel = (ContextModel) o;
        return this.numOfContexts != contextModel.numOfContexts;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(numOfContexts).toHashCode();
    }
}
