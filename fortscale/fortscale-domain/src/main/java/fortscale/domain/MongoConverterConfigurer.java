package fortscale.domain;

import org.apache.commons.lang.StringUtils;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;

public class MongoConverterConfigurer {
	private MappingMongoConverter mongoConverter;
	private String mapKeyDotReplacement;
	
	
	public void init(){
		if(!StringUtils.isEmpty(mapKeyDotReplacement)){
			mongoConverter.setMapKeyDotReplacement(mapKeyDotReplacement);
		}
	}
	
	public MappingMongoConverter getMongoConverter() {
		return mongoConverter;
	}
	public void setMongoConverter(MappingMongoConverter mongoConverter) {
		this.mongoConverter = mongoConverter;
	}
	public String getMapKeyDotReplacement() {
		return mapKeyDotReplacement;
	}
	public void setMapKeyDotReplacement(String mapKeyDotReplacement) {
		this.mapKeyDotReplacement = mapKeyDotReplacement;
	} 
}
