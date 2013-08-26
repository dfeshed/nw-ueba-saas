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
	public static final String userIdField = "userId";
	public static final String scoreField = "score";
	
	
	
	@Indexed(unique = false)
	@Field(userIdField)
	private String userId;
	@Field(scoreField)
	private Double score;	
	

	@Transient
	private List<IFeature> attrVals;
	
	
	public List<IFeature> getAttrVals() {
		return attrVals;
	}


	public void setAttrVals(List<IFeature> attrVals) {
		this.attrVals = attrVals;
	}


	@PersistenceConstructor
	public AdUserFeaturesExtraction(String userId) {

		this.userId = userId;
	}
	

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	
	public Double getScore() {
		return score;
	}


	public void setScore(Double score) {
		this.score = score;
	}
}
