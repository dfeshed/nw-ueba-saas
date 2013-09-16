package fortscale.services.fe;

import java.util.List;


public interface IClassifierScoreDistribution {
	public String getClassifierId();
	public List<IScoreDistribution> getDist();
}
