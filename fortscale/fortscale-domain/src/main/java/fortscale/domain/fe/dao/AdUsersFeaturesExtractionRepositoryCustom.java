package fortscale.domain.fe.dao;

import java.util.Date;

import fortscale.domain.fe.AdUserFeaturesExtraction;

interface AdUsersFeaturesExtractionRepositoryCustom {
	public void saveMap(AdUserFeaturesExtraction adUsersFeaturesExtraction);
	public Double calculateAvgScore(Date timestamp);
}
