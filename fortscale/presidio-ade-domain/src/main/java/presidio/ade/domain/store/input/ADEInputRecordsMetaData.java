package presidio.ade.domain.store.input;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * meta data for input records, like who is there data source and what are their time range
 * Created by barak_schuster on 5/18/17.
 */
public class ADEInputRecordsMetaData {
    String DataSource;

    /**
     *
     * @return ToString you know...
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
