package fortscale.domain;

import fortscale.utils.mongodb.converter.FSMappingMongoConverter;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;

public class MongoConverterConfigurer {
	private FSMappingMongoConverter mongoConverter;
	private String mapKeyDotReplacement;
	private String mapKeyDollarReplacement;
	
	
	public void init(){
		if(!StringUtils.isEmpty(mapKeyDotReplacement)){
			mongoConverter.setMapKeyDotReplacement(mapKeyDotReplacement);
		}
		if(!StringUtils.isEmpty(mapKeyDollarReplacement))
		{
			mongoConverter.setMapKeyDollarReplacement(mapKeyDollarReplacement);
		}
	}
	
	public MappingMongoConverter getMongoConverter() {
		return mongoConverter;
	}
	public void setMongoConverter(FSMappingMongoConverter mongoConverter) {
		this.mongoConverter = mongoConverter;
	}
	public String getMapKeyDotReplacement() {
		return mapKeyDotReplacement;
	}
	public void setMapKeyDotReplacement(String mapKeyDotReplacement) {
		this.mapKeyDotReplacement = mapKeyDotReplacement;
	}

	public String getMapKeyDollarReplacement() {
		return mapKeyDollarReplacement;
	}

	public void setMapKeyDollarReplacement(String mapKeyDollarReplacement) {
		this.mapKeyDollarReplacement = mapKeyDollarReplacement;
	}
}
