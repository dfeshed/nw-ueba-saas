package fortscale.streaming.model.prevalance.field;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.math3.distribution.TDistribution;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

@JsonAutoDetect(fieldVisibility=Visibility.ANY, getterVisibility=Visibility.NONE, setterVisibility=Visibility.NONE)
public class ContinuousValuesModel {
	private Map<Double, Double> histogram = new HashMap<Double, Double>();
	private double roundNumber = 1;
	private int maxNumOfHistogramElements = 100;
	private double histogramAvg = 0;
	private double histogramStd = 0;
	private long N = 0;
	private boolean scoreForLargeValues = true;
	private boolean scoreForSmallValues = true;
	private double a2 = 100.0/3;
	private double a1 = 35.0/3;
	private double largestPValue = 0.2;
	
	
	@JsonCreator
	public ContinuousValuesModel(@JsonProperty("roundNumber") double roundNumber){
		if(roundNumber > 0){
			this.roundNumber = roundNumber;
		} else{
			this.roundNumber = 1;
		}
		
	}
	
	private double roundValue(double val){
		return roundNumber * (double) Math.round(val/roundNumber);
	}
	
	public void add(Double val) {
		if(val == null){
			return;
		}
		
		Double roundedVal = roundValue(val);
		Double count = histogram.containsKey(roundedVal) ? 1 + histogram.get(roundedVal) : 1;

		histogram.put(roundedVal, count);
		
		if(histogram.size() > maxNumOfHistogramElements){
			recalculateHistogram();
		}
		
		N++;
		calculateModel();
	}
	
	private void calculateModel(){
		double sum = 0;
		Iterator<Entry<Double, Double>> iter = histogram.entrySet().iterator();
		while(iter.hasNext()){
			Entry<Double,Double> entry = iter.next();
			sum += entry.getKey()*entry.getValue();
		}
		histogramAvg = sum/N;
		
		sum = 0;
		iter = histogram.entrySet().iterator();
		while(iter.hasNext()){
			Entry<Double,Double> entry = iter.next();
			sum += Math.pow(entry.getKey() - histogramAvg, 2)*entry.getValue();
		}
		histogramStd = Math.sqrt(sum/N);
	}
	
	private void recalculateHistogram(){
		while(histogram.size() > maxNumOfHistogramElements){
			Map<Double, Double> tmp = new HashMap<Double, Double>();
			roundNumber = roundNumber * 2;
			Iterator<Entry<Double, Double>> iter = histogram.entrySet().iterator();
			while(iter.hasNext()){
				Entry<Double,Double> entry = iter.next();
				Double roundedVal = roundValue((Double)entry.getKey());
				
				Double count = tmp.containsKey(roundedVal) ? entry.getValue() + tmp.get(roundedVal) : entry.getValue();

				tmp.put(roundedVal, count);
			}
			
			histogram = tmp;
		}
	}


	public double calculateScore(Double val) {
		if(histogramStd == 0){
			return 0;
		}
		
		if(val == null){
			return 0;
		}
		
		double roundedVal = roundValue(val);
		
		double ret = calculatScore(roundedVal);
		return ret*100;
	}
	
	private double calculatScore(double val){
		double z = (val - histogramAvg) / histogramStd;
		TDistribution tDistribution = new TDistribution(N-1);
		double p = tDistribution.density(z);
		if(p > largestPValue || (z > 0 && !scoreForLargeValues) || (z < 0 && !scoreForSmallValues)){
			return 0;
		}
		
		 return Math.max(a2*Math.pow(p, 2) - a1*p + 1, 0);
	}
	
	
	
	public int getMaxNumOfHistogramElements() {
		return maxNumOfHistogramElements;
	}

	public void setMaxNumOfHistogramElements(int maxNumOfHistogramElements) {
		this.maxNumOfHistogramElements = maxNumOfHistogramElements;
	}

	public boolean isScoreForLargeValues() {
		return scoreForLargeValues;
	}

	public void setScoreForLargeValues(boolean scoreForLargeValues) {
		this.scoreForLargeValues = scoreForLargeValues;
	}

	public boolean isScoreForSmallValues() {
		return scoreForSmallValues;
	}

	public void setScoreForSmallValues(boolean scoreForSmallValues) {
		this.scoreForSmallValues = scoreForSmallValues;
	}

	public double getA2() {
		return a2;
	}

	public void setA2(double a2) {
		this.a2 = a2;
	}

	public double getA1() {
		return a1;
	}

	public void setA1(double a1) {
		this.a1 = a1;
	}

	public double getLargestPValue() {
		return largestPValue;
	}

	public void setLargestPValue(double largestPValue) {
		this.largestPValue = largestPValue;
	}
}
