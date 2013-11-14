package fortscale.domain.fe;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.mongodb.DBObject;



@Component
public class FeatureReadConverter implements Converter<DBObject, IFeature>{

	@Override
	public IFeature convert(DBObject source) {
		Double score = (Double)source.get(ADFeature.FEATURE_SCORE_FIELD);
		Double value = (Double)source.get(ADFeature.FEATURE_VALUE_FIELD);
		
		DBObject explanation = (DBObject)source.get(ADFeature.FEATURE_EXPLANATION_FIELD);
		FeatureExplanation featureExplanation = null;
		if(explanation != null){
			Double featureDistribution = (Double)explanation.get(FeatureExplanation.FEATURE_DISTRIBUTION_FIELD);
			Integer featureCount = (Integer)explanation.get(FeatureExplanation.FEATURE_COUNT_FIELD);
			String featureReference = (String)explanation.get(FeatureExplanation.FEATURE_REFERENCE_FIELD);
			featureExplanation = new FeatureExplanation(featureDistribution, featureCount, featureReference);
		}
		
		ADFeature adFeature = new ADFeature((String)source.get(ADFeature.UNIQUE_NAME_FIELD), (String)source.get(ADFeature.DISPLAY_NAME_FIELD), value, score, featureExplanation);
		
		return adFeature;
	}

}
