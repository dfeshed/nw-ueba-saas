package fortscale.domain.fe;

import java.util.List;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;




@Document(collection=AdUserFeaturesExtraction.collectionName)
public class AdUserFeaturesExtraction extends AbstractFEDocument{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1878154991546450072L;
	public static final String collectionName =  "ad_user_features_extraction";
	public static final String classifierIdField = "classifierId";
	public static final String userIdField = "userId";
	public static final String rawIdField = "rawId";
	public static final String scoreField = "score";
	public static final String timestampEpochField = "timestampepoch";
	public static final String attrListField = "attributes";
	
	
	
	@Indexed(unique = false)
	@Field(classifierIdField)
	private String classifierId;
	@Indexed(unique = false)
	@Field(userIdField)
	private String userId;
	@Indexed(unique = false)
	@Field(rawIdField)
	private String rawId;
	@Field(scoreField)
	private Double score;
	@Field(timestampEpochField)
	private Long timestampepoch;
	@Field(attrListField)
	private List<IFeature> attributes;
	



	@PersistenceConstructor
	@JsonCreator
	public AdUserFeaturesExtraction(@JsonProperty("classifierId") String classifierId, @JsonProperty("userId") String userId, @JsonProperty("rawId") String rawId) {
		this.classifierId = classifierId;
		this.userId = userId;
		this.rawId = rawId;
	}
	
	public String getClassifierId() {
		return classifierId;
	}


	public void setClassifierId(String classifierId) {
		this.classifierId = classifierId;
	}


	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	
	public String getRawId() {
		return rawId;
	}


	public void setRawId(String rawId) {
		this.rawId = rawId;
	}


	public Double getScore() {
		return score;
	}


	public void setScore(Double score) {
		this.score = score;
	}


	public List<IFeature> getAttributes() {
		return attributes;
	}


	public void setAttributes(List<IFeature> attributes) {
		this.attributes = attributes;
	}

	public Long getTimestampepoch() {
		return timestampepoch;
	}

	public void setTimestampepoch(Long timestampepoch) {
		this.timestampepoch = timestampepoch;
	}
	
	public static String getFeatureScoreField() {
		return String.format("%s.%s", AdUserFeaturesExtraction.attrListField, ADFeature.FEATURE_SCORE_FIELD);
	}
	
	public static String getFeatureUniqueNameField() {
		return String.format("%s.%s", AdUserFeaturesExtraction.attrListField, ADFeature.UNIQUE_NAME_FIELD);
	}
	
	public static String getExplanationFeatureCountField() {
		return String.format("%s.%s.%s", AdUserFeaturesExtraction.attrListField, ADFeature.FEATURE_EXPLANATION_FIELD, FeatureExplanation.FEATURE_COUNT_FIELD);
	}
	
	public static String getExplanationFeatureDistributionField() {
		return String.format("%s.%s.%s", AdUserFeaturesExtraction.attrListField, ADFeature.FEATURE_EXPLANATION_FIELD, FeatureExplanation.FEATURE_DISTRIBUTION_FIELD);
	}	
}
