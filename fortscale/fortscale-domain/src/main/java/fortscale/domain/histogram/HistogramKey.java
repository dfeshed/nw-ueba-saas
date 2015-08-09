package fortscale.domain.histogram;

import java.util.List;
import java.util.Map;

/**
 * Marker interface for histograms
 *
 * @author gils
 * Date: 04/08/2015
 */
public interface HistogramKey {
	public List<String> generateKey();
}
