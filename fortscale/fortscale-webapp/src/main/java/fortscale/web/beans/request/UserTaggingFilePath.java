package fortscale.web.beans.request;

/**
 * Created by alexp on 07/03/2017.
 */
public class UserTaggingFilePath {
    String userTaggingFilePath;

    public UserTaggingFilePath() {
    }

    public UserTaggingFilePath(String systemSetupUserTaggingFilePath) {
        this.userTaggingFilePath = systemSetupUserTaggingFilePath;
    }

    public String getUserTaggingFilePath() {
        return userTaggingFilePath;
    }

    public void setUserTaggingFilePath(String userTaggingFilePath) {
        this.userTaggingFilePath = userTaggingFilePath;
    }
}
