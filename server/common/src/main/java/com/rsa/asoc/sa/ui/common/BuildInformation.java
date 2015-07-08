package com.rsa.asoc.sa.ui.common;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Date;

/**
 * A configuration bean to map the build related properties to.
 *
 * @author athielke
 * @since 11.0.0.0
 */
@ConfigurationProperties(prefix = "build")
public class BuildInformation {
    private String version;
    private Integer commit;
    private String changeset;
    private Date date;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Integer getCommit() {
        return commit;
    }

    public void setCommit(Integer commit) {
        this.commit = commit;
    }

    public String getChangeset() {
        return changeset;
    }

    public void setChangeset(String changeset) {
        this.changeset = changeset;
    }

    public Date getDate() {
        return (date != null) ? new Date(date.getTime()) : null;
    }

    public void setDate(Date date) {
        this.date = (date != null) ? new Date(date.getTime()) : null;
    }
}
