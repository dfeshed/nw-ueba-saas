package fortscale.domain.core.alert.analystfeedback;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.annotations.ApiModel;
import org.joda.time.DateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Auditable;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;

/**
 * Created by alexp on 02/02/17.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({ @JsonSubTypes.Type(value = AnalystRiskFeedback.class, name = AnalystRiskFeedback.ANALYST_RISK_FEEDBACK_TYPE),
        @JsonSubTypes.Type(value = AnalystCommentFeedback.class, name = AnalystCommentFeedback.ANALYST_COMMENT_FEEDBACK_TYPE) })
@ApiModel(subTypes = {AnalystRiskFeedback.class, AnalystCommentFeedback.class})
@Document(collection = "alert_comments")
public abstract class AnalystFeedback implements Auditable<String, String> {

    public static final String ANALYST_USER_NAME_FIELD = "analystUserName";
    private static final String MODIFIED_AT_FIELD = "modifiedAt";
    public static final String COLLECTION_NAME = "alert_comments";

    private static final long serialVersionUID = 5043063490239869442L;



    @Id
    private String id;

    @Field(ANALYST_USER_NAME_FIELD)
    private String analystUserName;

    @Field(MODIFIED_AT_FIELD)
    private Long modifiedAt;

    @Indexed
    private String alertId;

    private DateTime lastModifiedDate;
    private String lastModifiedBy;
    private DateTime creationDate;
    private String createdBy;

    public AnalystFeedback() {
    }

    public AnalystFeedback(String analystUserName, Long modifiedAt, String alertId) {
        this.analystUserName = analystUserName;
        this.modifiedAt = modifiedAt;
        this.alertId = alertId;

    }

    public AnalystFeedback(String commentId, String analystUserName, Long modifiedAt, String alertId) {
        this.setId(commentId);
        this.analystUserName = analystUserName;
        this.modifiedAt = modifiedAt;
        this.alertId = alertId;

    }

    public String getAnalystUserName() {
        return analystUserName;
    }

    public void setAnalystUserName(String analystUserName) {
        this.analystUserName = analystUserName;
    }


    public Long getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(Long modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAlertId() {
        return alertId;
    }

    public void setAlertId(String alertId) {
        this.alertId = alertId;
    }





    @Override
    public String getCreatedBy() {
        return this.createdBy;
    }

    @Override
    public void setCreatedBy(String createdBy) {

        this.createdBy = createdBy;
    }

    @Override
    public DateTime getCreatedDate() {
        return creationDate;
    }

    @Override
    public void setCreatedDate(DateTime creationDate) {
        this.creationDate=creationDate;
    }

    @Override
    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    @Override
    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    @Override
    public DateTime getLastModifiedDate() {
        return lastModifiedDate;
    }

    @Override
    public void setLastModifiedDate(DateTime lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }




    @Override
    @JsonIgnore
    public boolean isNew() {
        return getId() == null;
    }

    @Override
    public String getId() {
        return id;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AnalystFeedback that = (AnalystFeedback) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (analystUserName != null ? !analystUserName.equals(that.analystUserName) : that.analystUserName != null)
            return false;
        if (modifiedAt != null ? !modifiedAt.equals(that.modifiedAt) : that.modifiedAt != null) return false;
        return alertId != null ? alertId.equals(that.alertId) : that.alertId == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (analystUserName != null ? analystUserName.hashCode() : 0);
        result = 31 * result + (modifiedAt != null ? modifiedAt.hashCode() : 0);
        result = 31 * result + (alertId != null ? alertId.hashCode() : 0);
        return result;
    }


}