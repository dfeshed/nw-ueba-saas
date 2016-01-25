package fortscale.domain.core;

import fortscale.utils.prettifiers.BytesPrettifier;
import fortscale.utils.prettifiers.NumbersPrettifier;
import fortscale.utils.spring.SpringPropertiesUtil;
import fortscale.utils.time.TimeUtils;

import java.util.Date;
import java.util.List;

/**
 * Created by avivs on 20/01/16.
 */
public class EmailEvidenceDecorator extends Evidence{

    public String getName() {
        return name;
    }

    public String getDataSource() {
        return dataSource;
    }

    public String getPrettifiedAnomalyValue() {
        return prettifiedAnomalyValue;
    }

    public String getPrettyStartDate() {
        return prettyStartDate;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public void setPrettifiedAnomalyValue(String prettifiedAnomalyValue) {
        this.prettifiedAnomalyValue = prettifiedAnomalyValue;
    }

    public void setPrettyStartDate(String prettyStartDate) {
        this.prettyStartDate = prettyStartDate;
    }

    private String name = "";
    private String dataSource = "";
    private String prettifiedAnomalyValue = "";
    private String prettyStartDate = "";


    public EmailEvidenceDecorator() {}

    /**
     *
     * @param evidence The evidence to be decorated
     */
    public EmailEvidenceDecorator(Evidence evidence) {
        super(evidence);
    }


}
