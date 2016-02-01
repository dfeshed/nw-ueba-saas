package fortscale.common.feature.extraction;

import java.util.ArrayList;
import java.util.List;

import fortscale.common.event.Event;
import fortscale.common.feature.FeatureStringValue;
import fortscale.common.feature.FeatureValue;
import org.apache.commons.lang.StringUtils;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;


@JsonTypeName(PriorityContainerFeatureExtractor.PRIORITY_CONTAINER_FEATURE_EXTRACTOR_TYPE)
public class PriorityContainerFeatureExtractor implements FeatureExtractor {
	protected static final String PRIORITY_CONTAINER_FEATURE_EXTRACTOR_TYPE = "priority_container_feature_extractor";

	private List<FeatureExtractor> featureExtractorList = new ArrayList<>();

	public PriorityContainerFeatureExtractor(){}

	public PriorityContainerFeatureExtractor(@JsonProperty("featureExtractorList")List<FeatureExtractor> featureExtractorList){
		this.featureExtractorList = featureExtractorList;
	}


	@Override
	public FeatureValue extract(Event event) throws Exception {
		FeatureValue ret = null;
		for(FeatureExtractor extractor: featureExtractorList){
			ret = extractor.extract(event);
			if(ret != null && ret instanceof FeatureStringValue){
				if(!StringUtils.isBlank(((FeatureStringValue)ret).getValue())){
					break;
				}
			}else{
				if(ret != null ){
					break;
				}
			}
		}

		return ret;
	}

	public List<FeatureExtractor> getFeatureExtractorList() {
		return featureExtractorList;
	}

	public void setFeatureExtractorList(List<FeatureExtractor> featureExtractorList) {
		this.featureExtractorList = featureExtractorList;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		PriorityContainerFeatureExtractor that = (PriorityContainerFeatureExtractor) o;

		if(!featureExtractorList.equals(that.featureExtractorList)){
			return false;
		}

		return true;
	}
	
	@Override public int hashCode() {
		if(featureExtractorList.size() > 0){
			return featureExtractorList.get(0).hashCode();
		} else{
			return 0;
		}
	}
}
