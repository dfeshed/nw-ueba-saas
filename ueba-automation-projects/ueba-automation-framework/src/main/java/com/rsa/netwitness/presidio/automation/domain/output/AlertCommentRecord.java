package com.rsa.netwitness.presidio.automation.domain.output;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "alert_comments")
public class AlertCommentRecord {

    @Id
    private Object _id;

    private String _class; //"fortscale.domain.core.alert.analystfeedback.AnalystCommentFeedback",
    private String alertId;
    private String analystUserName;
    private String commentText;
    private Date creationDate;
    private Date lastModifiedDate;
    private long modifiedAt;

    @Override
    public String toString() {
        return "AlertCommentRecord{" +
                "_id='" + _id + '\'' +
                ", _class='" + _class + '\'' +
                ", commentText='" + commentText + '\'' +
                ", analystUserName='" + analystUserName + '\'' +
                ", modifiedAt='" + modifiedAt + '\'' +
                ", alertId='" + alertId + '\'' +
                ", lastModifiedDate='" + lastModifiedDate + '\'' +
                ", creationDate='" + creationDate + '\'' +
                '}';
    }

    public Object get_id() {
        return _id;
    }

    public void set_id(Object _id) {
        this._id = _id;
    }

    public String get_class() {
        return _class;
    }

    public void set_class(String _class) {
        this._class = _class;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public String getAnalystUserName() {
        return analystUserName;
    }

    public void setAnalystUserName(String analystUserName) {
        this.analystUserName = analystUserName;
    }

    public String getAlertId() {
        return alertId;
    }

    public void setAlertId(String alertId) {
        this.alertId = alertId;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public long getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(long modifiedAt) {
        this.modifiedAt = modifiedAt;
    }
}
