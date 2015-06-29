package fortscale.streaming.aggregation.feature.extraction;

import java.util.ArrayList;
import java.util.List;

import net.minidev.json.JSONObject;

import org.apache.commons.lang.StringUtils;

import com.fasterxml.jackson.annotation.JsonTypeName;


@JsonTypeName(ChainFeatureAdjustor.CHAIN_FEATURE_ADJUSTOR_TYPE)
public class ChainFeatureAdjustor implements FeatureAdjustor {
	protected static final String CHAIN_FEATURE_ADJUSTOR_TYPE = "chain_feature_adjustor";

	private List<FeatureAdjustor> featureAdjustorList = new ArrayList<>();

	public ChainFeatureAdjustor(){}

	public ChainFeatureAdjustor(List<FeatureAdjustor> featureAdjustorList){
		this.featureAdjustorList = featureAdjustorList;
	}

	@Override
	public Object adjust(Object value, JSONObject message) {
		Object ret = value;
		for(FeatureAdjustor adjustor: featureAdjustorList){
			ret = adjustor.adjust(ret, message);
			if(ret == null || (ret instanceof String && StringUtils.isBlank((String) ret))){
				break;
			}
		}

		return ret;
	}





	public List<FeatureAdjustor> getFeatureAdjustorList() {
		return featureAdjustorList;
	}

	public void setFeatureAdjustorList(List<FeatureAdjustor> featureAdjustorList) {
		this.featureAdjustorList = featureAdjustorList;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		ChainFeatureAdjustor that = (ChainFeatureAdjustor) o;
		if(!featureAdjustorList.equals(that.featureAdjustorList))
			return false;
		return true;
	}
	
	@Override
	public int hashCode() {
		if(featureAdjustorList.size()>0){
			return featureAdjustorList.get(0).hashCode();
		} else{
			return CHAIN_FEATURE_ADJUSTOR_TYPE.hashCode();
		}
	}
	
}
