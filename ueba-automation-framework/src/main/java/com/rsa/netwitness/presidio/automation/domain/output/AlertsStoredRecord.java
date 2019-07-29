package com.rsa.netwitness.presidio.automation.domain.output;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.testng.collections.Maps;

import java.util.*;

public class AlertsStoredRecord {
    @Expose
    private String id;
    @Expose
    private String[] classification;
    @Expose
    private String entityName;
    @Expose
    private String[] indicatorsName;
    @Expose
    private Integer indicatorsNum;
    @Expose
    private String score;
    @Expose
    private String feedback;
    @Expose
    private String entityScoreContribution;
    @Expose
    private String timeframe;
    @Expose
    private String severity;
    @Expose
    private String entityDocumentId;
    @Expose
    private List<Indicator> indicatorsList;
    @Expose
    private String startDate;
    @Expose
    private String endDate;

    public AlertsStoredRecord() {
    }

    public AlertsStoredRecord(String id, String[] classification, String entityName, String[] indicatorsName, Integer indicatorsNum, String score, String feedback, String entityScoreContribution, String timeframe, String severity, String entityDocumentId, String startDate, String endDate) {
        this.id = id;
        this.classification = classification;
        this.entityName = entityName;
        this.indicatorsName = indicatorsName;
        this.indicatorsNum = indicatorsNum;
        this.score = score;
        this.feedback = feedback;
        this.entityScoreContribution = entityScoreContribution;
        this.timeframe = timeframe;
        this.severity = severity;
        this.entityDocumentId = entityDocumentId;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public AlertsStoredRecord(String id, String[] classification, String entityName, String[] indicatorsName, Integer indicatorsNum, String score, String feedback, String entityScoreContribution, String timeframe, String severity, String entityDocumentId, JSONArray indicators, String startDate, String endDate) {
        this.id = id;
        this.classification = classification;
        this.entityName = entityName;
        this.indicatorsName = indicatorsName;
        this.indicatorsNum = indicatorsNum;
        this.score = score;
        this.feedback = feedback;
        this.entityScoreContribution = entityScoreContribution;
        this.timeframe = timeframe;
        this.severity = severity;
        this.entityDocumentId = entityDocumentId;
        this.startDate = startDate;
        this.endDate = endDate;
        setIndicatorsList(indicators);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String[] getClassification() {
        return classification;
    }

    public void setClassification(String[] classification) {
        this.classification = classification;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public String[] getIndicatorsName() {
        return indicatorsName;
    }

    public void setIndicatorsName(String[] indicatorsName) {
        this.indicatorsName = indicatorsName;
    }

    public Integer getIndicatorsNum() {
        return indicatorsNum;
    }

    public void setIndicatorsNum(Integer indicatorsNum) {
        this.indicatorsNum = indicatorsNum;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public String getEntityScoreContribution() {
        return entityScoreContribution;
    }

    public void setEntityScoreContribution(String entityScoreContribution) {
        this.entityScoreContribution = entityScoreContribution;
    }

    public String getTimeframe() {
        return timeframe;
    }

    public void setTimeframe(String timeframe) {
        this.timeframe = timeframe;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getEntityDocumentId() {
        return entityDocumentId;
    }

    public void setEntityDocumentId(String entityDocumentId) {
        this.entityDocumentId = entityDocumentId;
    }

    public List<Indicator> getIndicatorsList() {
        return indicatorsList;
    }

    private void setIndicatorsList(JSONArray indicators) {
        indicatorsList = new ArrayList<>();
        for (int i = 0; i < indicators.length(); i++) {

            try {
                Indicator indicator = new Indicator();
                JSONObject obj = indicators.getJSONObject(i);
                indicator.setId(obj.get("id").toString());
                indicator.setName(obj.get("name").toString());
                indicator.setAnomalyValue(obj.get("anomalyValue").toString());
                indicator.setSchema(obj.get("schema").toString());
                indicator.setType(obj.get("type").toString());
                indicator.setScore(obj.get("score").toString());
                indicator.setEventNum(obj.get("eventsNum").toString());
                indicator.setStartDate(obj.get("startDate").toString());
                indicator.setEndDate(obj.get("endDate").toString());
                indicator.setScoreContribution(obj.getDouble("scoreContribution"));
                indicator.setContexts(obj);

                indicatorsList.add(indicator);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }


    @Override
    public String toString() {
        return "AlertsStoredRecord{" +
                "id='" + id + '\'' +
                ", classification=" + Arrays.toString(classification) +
                ", entityName='" + entityName + '\'' +
                ", indicatorsName=" + Arrays.toString(indicatorsName) +
                ", indicatorsNum=" + indicatorsNum +
                ", score='" + score + '\'' +
                ", feedback='" + feedback + '\'' +
                ", entityScoreContribution='" + entityScoreContribution + '\'' +
                ", timeframe='" + timeframe + '\'' +
                ", severity='" + severity + '\'' +
                ", entityDocumentId='" + entityDocumentId + '\'' +
                ", indicatorsList=" + indicatorsList +
                ", startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                '}';
    }


    public class Indicator {

        private String id;
        private String name;
        private String anomalyValue;
        private String schema;
        private String type;
        private String score;
        private String eventNum;
        private String startDate;
        private String endDate;
        private Double scoreContribution;
        private Map<String, Object> contexts = Maps.newHashMap();

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAnomalyValue() {
            return anomalyValue;
        }

        public void setAnomalyValue(String anomalyValue) {
            this.anomalyValue = anomalyValue;
        }

        public String getSchema() {
            return schema;
        }

        public void setSchema(String schema) {
            this.schema = schema;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getScore() {
            return score;
        }

        public void setScore(String score) {
            this.score = score;
        }

        public String getEventNum() {
            return eventNum;
        }

        public void setEventNum(String eventNum) {
            this.eventNum = eventNum;
        }

        public String getStartDate() {
            return startDate;
        }

        public void setStartDate(String startDate) {
            this.startDate = startDate;
        }

        public String getEndDate() {
            return endDate;
        }

        public void setEndDate(String endDate) {
            this.endDate = endDate;
        }

        public Double getScoreContribution() {
            return scoreContribution;
        }

        public void setScoreContribution(Double scoreContribution) {
            this.scoreContribution = scoreContribution;
        }

        public Map<String, Object> getContexts() {
            return contexts;
        }

        public void setContexts(JSONObject obj) {
            if (obj.has("contexts")) {
                JSONObject contexts = obj.getJSONObject("contexts");
                this.contexts = new Gson().fromJson(contexts.toString(), HashMap.class);
            }
        }

        @Override
        public String toString() {
            return "Indicator{" +
                    "id='" + id + '\'' +
                    ", name='" + name + '\'' +
                    ", anomalyValue='" + anomalyValue + '\'' +
                    ", schema='" + schema + '\'' +
                    ", type='" + type + '\'' +
                    ", score='" + score + '\'' +
                    ", eventNum='" + eventNum + '\'' +
                    ", startDate='" + startDate + '\'' +
                    ", endDate='" + endDate + '\'' +
                    ", scoreContribution=" + scoreContribution + '\'' +
                    ", contexts=[" + contexts + ']' +
                    '}';
        }
    }
}
