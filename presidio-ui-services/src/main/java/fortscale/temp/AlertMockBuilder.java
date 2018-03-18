package fortscale.temp;

import fortscale.domain.core.*;

import java.util.*;

/**
 * Created by shays on 05/07/2017.
 */
public class AlertMockBuilder {


    private final AlertFeedback feedback;
    private final AlertStatus status;
    private final String entityId;
    private final AlertTimeframe timeframe;
    private final Set<DataSourceAnomalyTypePair> dataSourceAnomalyTypePair;
    private final int userScoreContribution;
    private final boolean userScoreContributionFlag;
    private int severityCode;
    private final Date startDate;
    private final Date endDate;
    private final EntityType entityType;
    private final String entityName;
    private String id;
    private String name;
    private String userId ;
    private int score;
    private Severity severity;
    private List<Evidence> evidences = new ArrayList<>();

    public AlertMockBuilder(int serial){
        this.id=id+1;
        this.name="alert"+serial;
        this.userId = "user"+serial;
        this.score = HardCodedMocks.DEFAULT_SCORE;
        this.severity = Severity.Medium;
        this.evidences.add(MockScenarioGenerator.generateMocksEvidence());
        this.severityCode = this.severity.ordinal();

        this.startDate = getMinusHours(2);

        this.endDate = getMinusHours(1);
        this.entityType = EntityType.User;
        this.entityName = "user@bigcompany.com";



        this.severityCode = this.severity.ordinal();
        this.status = AlertStatus.Open;
        this.feedback = AlertFeedback.None;
        this.entityId = "0000-0000-0000";
        this.timeframe = AlertTimeframe.Hourly;

        this.dataSourceAnomalyTypePair = new HashSet<>();
        this.userScoreContribution = 50;
        this.userScoreContributionFlag = true;
    }

    private Date getMinusHours(int hours){
        Calendar c = Calendar.getInstance();
        c.add(Calendar.HOUR,-1);
        return c.getTime();
    }

    public Alert createInstance(){
        Alert alert = new Alert();


        alert.setMockId(this.id);
        alert.setName(this.name);

        alert.setScore(this.score);
        alert.setSeverity(this.severity);
        alert.setEvidences(this.evidences);
        alert.setEvidenceSize(this.evidences == null ? 0 : this.evidences.size());
        alert.setEntityId(userId);
        alert.setFeedback(feedback);

        alert.setStatus(status);
        alert.setDataSourceAnomalyTypePair(dataSourceAnomalyTypePair);
        alert.setStartDate(this.startDate.getTime());
        alert.setEndDate(endDate.getTime());
        alert.setTimeframe(timeframe);
        alert.setUserScoreContribution(userScoreContribution);
        alert.setUserScoreContributionFlag(userScoreContributionFlag);
        return alert;
    }
}
