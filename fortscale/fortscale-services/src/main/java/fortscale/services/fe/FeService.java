package fortscale.services.fe;

import java.util.Date;
import java.util.List;
import java.util.Map;

import fortscale.domain.ad.AdUser;
import fortscale.domain.fe.IFeature;

public interface FeService {
	public Iterable<AdUser> getAdUsersAttrVals();
	
	public void setAdUsersScores(Map<String, Double> userScoresMap, Map<String, List<IFeature>> userFeaturesScoresMap, Date timeStamp);
	
}
