package fortscale.ml.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@JsonAutoDetect(
        fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE,
        setterVisibility = Visibility.NONE, isGetterVisibility = Visibility.NONE)
public class ContextModel implements Model {
    private int numOfContexts;

    public ContextModel(int numOfContexts) {
        this.numOfContexts = numOfContexts;
    }

    public int getNumOfContexts() {
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
        ContextModel timeModel = (ContextModel) o;
        return this.numOfContexts != timeModel.numOfContexts;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(numOfContexts).toHashCode();
    }
}
