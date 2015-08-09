package fortscale.domain.histogram;

/**
 * Interface for histogram keys
 *
 * @author gils
 * Date: 04/08/2015
 */
public interface HistogramKey {
    String serialize(HistogramKeyVisitor histogramKeyVisitor);
}
