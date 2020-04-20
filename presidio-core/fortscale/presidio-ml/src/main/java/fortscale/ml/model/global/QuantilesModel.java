//package fortscale.ml.model.prevalance.field;
//
//import com.fasterxml.jackson.annotation.JsonAutoDetect;
//import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
//import fortscale.ml.model.prevalance.FieldModel;
//import fortscale.utils.ConversionUtils;
//import org.apache.samza.config.Config;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//
//@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
//public class QuantilesModel implements FieldModel {
//	private static final Logger logger = LoggerFactory.getLogger(QuantilesModel.class);
//	public static final int NUM_OF_QUANTILES = 100;
//
//	private List<Double> quantiles;
//
//	/**
//	 * QuantilesModel constructor.
//	 */
//	public QuantilesModel() {
//		quantiles = new ArrayList<>(Collections.nCopies(NUM_OF_QUANTILES, 0.0));
//	}
//
//	/**
//	 * Updates the value of a specific quantile in the model.
//	 *
//	 * @param quantile the index of the quantile that will be updated (must be an Integer between 1 and NUM_OF_QUANTILES).
//	 * @param value    the new value that will be set to the specific quantile.
//	 */
//	public void setQuantile(Integer quantile, Double value) {
//		if (quantile == null) {
//			logger.warn("Missing quantile index");
//		} else if (!(quantile >= 1 && quantile <= NUM_OF_QUANTILES)) {
//			logger.warn("Expected an index between 1 and {}, found {}", NUM_OF_QUANTILES, quantile);
//		} else if (value == null || value.isNaN()) {
//			logger.warn("Missing new value for the given quantile");
//		} else {
//			quantiles.set(quantile - 1, value);
//		}
//	}
//
//	/**
//	 * Given a value, returns the quantile in the model to which it belongs (the inverse function of getQuantile).
//	 * 1. If the value falls between two quantiles, the function returns the greater one.
//	 * 2. If the value is greater than the one of the upper quantile, the function returns the upper quantile.
//	 * 3. If the value belongs to several consecutive quantiles (with equal values), the function returns the median quantile.
//	 *
//	 * @param value the given value.
//	 * @return the quantile to which the given value belongs.
//	 */
//	private Integer inverseQuantile(Double value) {
//		for (int i = 0; i < NUM_OF_QUANTILES; i++) {
//			Double ithQuantile = quantiles.get(i);
//
//			if (value < ithQuantile) {
//				return i + 1;
//			} else if (value.equals(ithQuantile)) {
//				int j = i + 1;
//				while (j < NUM_OF_QUANTILES && quantiles.get(j).equals(ithQuantile)) {
//					j++;
//				}
//
//				// [i, j - 1] is the interval of consecutive quantiles with equal values
//				int intervalSize = j - i;
//				// return the median quantile and convert to a one-based index
//				return i + (intervalSize / 2) + 1;
//			}
//		}
//
//		return NUM_OF_QUANTILES;
//	}
//
//	@Override
//	public double calculateScore(Object value) {
//		Double valueAsDouble = ConversionUtils.convertToDouble(value);
//
//		if (valueAsDouble == null)
//			return 0;
//		else
//			return inverseQuantile(valueAsDouble) / (double)NUM_OF_QUANTILES;
//	}
//
//	/* 'init' and 'add' are redundant, as initialization
//	 * and model building is done by the Model Builder. */
//	@Override
//	public void init(String prefix, String fieldName, Config config) {}
//
//	@Override
//	public void add(Object value, long timestamp) {}
//
//	@Override
//	public long getNumOfSamples() {
//		return 0;
//	}
//}
