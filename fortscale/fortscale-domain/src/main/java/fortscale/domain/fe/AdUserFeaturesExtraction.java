package fortscale.domain.fe;

import java.util.List;

import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;




@Document(collection=AdUserFeaturesExtraction.collectionName)
public class AdUserFeaturesExtraction extends AbstractFEDocument{
	public static final String collectionName =  "ad_user_features_extraction";
	public static final String classifierIdField = "classifierId";
	public static final String userIdField = "userId";
	public static final String rawIdField = "rawId";
	public static final String scoreField = "score";
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
	@Field(attrListField)
	private List<IFeature> attributes;
	

	@Transient
	private List<IFeature> attrVals;
	
	
	public List<IFeature> getAttrVals() {
		return attrVals;
	}


	public void setAttrVals(List<IFeature> attrVals) {
		this.attrVals = attrVals;
	}


	@PersistenceConstructor
	public AdUserFeaturesExtraction(String classifierId, String userId, String rawId) {
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
	
	
}
