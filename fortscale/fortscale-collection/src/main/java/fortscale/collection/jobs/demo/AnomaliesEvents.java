package fortscale.collection.jobs.demo;

import fortscale.domain.core.EvidenceTimeframe;
import fortscale.domain.core.EvidenceType;

/**
 * Created by Amir Keren on 17/02/2016.
 */
public class AnomaliesEvents {

    private DemoUtils.DataSource dataSource;
    private DemoEvent demoEvent;
    private int minNumberOfAnomalies;
    private int maxNumberOfAnomalies;
    private int minHourForAnomaly;
    private int maxHourForAnomaly;
    private EvidenceTimeframe timeframe;
    private EvidenceType evidenceType;
    private String anomalyTypeFieldName;

    public DemoUtils.DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DemoUtils.DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public DemoEvent getDemoEvent() {
        return demoEvent;
    }

    public void setDemoEvent(DemoEvent demoEvent) {
        this.demoEvent = demoEvent;
    }

    public int getMinNumberOfAnomalies() {
        return minNumberOfAnomalies;
    }

    public void setMinNumberOfAnomalies(int minNumberOfAnomalies) {
        this.minNumberOfAnomalies = minNumberOfAnomalies;
    }

    public int getMaxNumberOfAnomalies() {
        return maxNumberOfAnomalies;
    }

    public void setMaxNumberOfAnomalies(int maxNumberOfAnomalies) {
        this.maxNumberOfAnomalies = maxNumberOfAnomalies;
    }

    public int getMinHourForAnomaly() {
        return minHourForAnomaly;
    }

    public void setMinHourForAnomaly(int minHourForAnomaly) {
        this.minHourForAnomaly = minHourForAnomaly;
    }

    public int getMaxHourForAnomaly() {
        return maxHourForAnomaly;
    }

    public void setMaxHourForAnomaly(int maxHourForAnomaly) {
        this.maxHourForAnomaly = maxHourForAnomaly;
    }

    public EvidenceTimeframe getTimeframe() {
        return timeframe;
    }

    public void setTimeframe(EvidenceTimeframe timeframe) {
        this.timeframe = timeframe;
    }

    public EvidenceType getEvidenceType() {
        return evidenceType;
    }

    public void setEvidenceType(EvidenceType evidenceType) {
        this.evidenceType = evidenceType;
    }

    public String getAnomalyTypeFieldName() {
        return anomalyTypeFieldName;
    }

    public void setAnomalyTypeFieldName(String anomalyTypeFieldName) {
        this.anomalyTypeFieldName = anomalyTypeFieldName;
    }

}