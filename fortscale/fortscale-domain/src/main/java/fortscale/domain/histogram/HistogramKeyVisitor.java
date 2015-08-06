package fortscale.domain.histogram;

/**
 * @author gils
 *         Date: 05/08/2015
 */
public interface HistogramKeyVisitor {
    String visit(HistogramSingleKey histogramSingleKey);
    String visit(HistogramDualKey histogramDualKey);
}
