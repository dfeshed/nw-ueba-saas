package fortscale.domain.fe;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;



@Component
public class FeatureWriteConverter implements Converter<IFeature, DBObject>{

	@Override
	public DBObject convert(IFeature source) {
		BasicDBObject featureObject = new BasicDBObject();
		featureObject.append(ADFeature.UNIQUE_NAME_FIELD, source.getFeatureUniqueName());
		featureObject.append(ADFeature.DISPLAY_NAME_FIELD, source.getFeatureDisplayName());
		featureObject.append(ADFeature.FEATURE_SCORE_FIELD, source.getFeatureScore());
		featureObject.append(ADFeature.FEATURE_VALUE_FIELD, source.getFeatureValue());
		
		BasicDBObject featureExplanationObject = new BasicDBObject();
		featureExplanationObject.append(FeatureExplanation.FEATURE_COUNT_FIELD, source.getFeatureExplanation().getFeatrueCount());
		featureExplanationObject.append(FeatureExplanation.FEATURE_DISTRIBUTION_FIELD, source.getFeatureExplanation().getFeatureDistribution());
		featureExplanationObject.append(FeatureExplanation.FEATURE_REFERENCE_FIELD, source.getFeatureExplanation().getFeatureReference());
		
		featureObject.append(ADFeature.FEATURE_EXPLANATION_FIELD, featureExplanationObject);
		
		return featureObject;
	}

}
