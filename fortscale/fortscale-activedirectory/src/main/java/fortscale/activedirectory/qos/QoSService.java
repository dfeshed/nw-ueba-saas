package fortscale.activedirectory.qos;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

import fortscale.domain.ad.AdUser;
import fortscale.domain.fe.IFeature;
import fortscale.services.fe.FeService;

public class QoSService implements FeService {
	
	private FeService feService;
	private QoS qos;
	private String qosResult;
	private int qosSuccessRate;
	
	public QoSService(FeService feService) {
		this.feService = feService;
		qos = new QoS();
		
	}

	@Override
	public Iterable<AdUser> getAdUsersAttrVals() {
		Iterable<AdUser> validUsers = feService.getAdUsersAttrVals(); 
		return qos.generateQoSTestUsers(validUsers);
	}

	@Override
	public void setAdUsersScores(Map<String, Double> userScoresMap, Map<String, Collection<IFeature>> userFeaturesScoresMap, Date timeStamp) {
		qos.computeTestResults(userScoresMap);
		qosResult = qos.getTestResults();
		qosSuccessRate = qos.getTestSuccessRate();
		this.feService.setAdUsersScores(userScoresMap, userFeaturesScoresMap, timeStamp);
	}
	

	public String getQosResult() {
		return qosResult;
	}
	
	public int getQosSuccessRate() {
		return qosSuccessRate;
	}
}
