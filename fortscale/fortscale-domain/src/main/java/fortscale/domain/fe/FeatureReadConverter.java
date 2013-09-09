package fortscale.domain.fe;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.mongodb.DBObject;



@Component
public class FeatureReadConverter implements Converter<DBObject, IFeature>{

	@Override
	public IFeature convert(DBObject source) {
		ADFeature adFeature = new ADFeature((String)source.get(ADFeature.UNIQUE_NAME_FIELD), (String)source.get(ADFeature.DISPLAY_NAME_FIELD), 
				(Double)source.get(ADFeature.FEATURE_VALUE_FIELD),(Double)source.get(ADFeature.FEATURE_SCORE_FIELD));
		return adFeature;
	}

}
