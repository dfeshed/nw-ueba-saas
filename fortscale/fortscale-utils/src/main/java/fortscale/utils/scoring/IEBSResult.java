package fortscale.utils.scoring;

import java.util.List;
import java.util.Map;

public interface IEBSResult {
	public List<Map<String, Object>> getResultsList();
	public Double getGlobalScore();
	public int getOffset();
	public int getTotal();
}
