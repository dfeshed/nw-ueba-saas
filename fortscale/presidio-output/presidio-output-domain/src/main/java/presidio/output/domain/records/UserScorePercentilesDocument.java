package presidio.output.domain.records;


import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import presidio.output.domain.records.users.User;

/**
 * Holds the user score threshold for high\medium\low score according to the
 * last calculated scores percentiles
 *
 * Created by Efrat Noam on 12/5/17.
 */
@Document(indexName = User.INDEX_NAME, type = User.USER_SCORE_THRESHOLDS_DOC_TYPE)
public class UserScorePercentilesDocument extends AbstractElasticDocument {

    public static final String USER_SCORE_PERCENTILES_DOC_ID = "user-score-percentile-doc-id";

    @Field(type = FieldType.Double)
    private double ceilScoreForLowSeverity;

    @Field(type = FieldType.Double)
    private double ceilScoreForMediumSeverity;

    @Field(type = FieldType.Double)
    private double ceilScoreForHighSeverity;

    public UserScorePercentilesDocument() {}

    public UserScorePercentilesDocument(double ceilScoreForHighSeverity, double ceilScoreForMediumSeverity, double ceilScoreForLowSeverity) {
        setId(USER_SCORE_PERCENTILES_DOC_ID);
        this.ceilScoreForHighSeverity = ceilScoreForHighSeverity;
        this.ceilScoreForMediumSeverity = ceilScoreForMediumSeverity;
        this.ceilScoreForLowSeverity = ceilScoreForLowSeverity;

    }

    public double getCeilScoreForLowSeverity() {
        return ceilScoreForLowSeverity;
    }

    public double getCeilScoreForMediumSeverity() {
        return ceilScoreForMediumSeverity;
    }

    public double getCeilScoreForHighSeverity() {
        return ceilScoreForHighSeverity;
    }

    public void setCeilScoreForLowSeverity(double ceilScoreForLowSeverity) {
        this.ceilScoreForLowSeverity = ceilScoreForLowSeverity;
    }

    public void setCeilScoreForMediumSeverity(double ceilScoreForMediumSeverity) {
        this.ceilScoreForMediumSeverity = ceilScoreForMediumSeverity;
    }

    public void setCeilScoreForHighSeverity(double ceilScoreForHighSeverity) {
        this.ceilScoreForHighSeverity = ceilScoreForHighSeverity;
    }
}
