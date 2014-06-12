package fortscale.streaming.model.field;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

import fortscale.streaming.model.calibration.FeatureCalibration;
import fortscale.streaming.model.calibration.FeatureCalibrationBucketScorer;


@JsonAutoDetect(fieldVisibility=Visibility.ANY, getterVisibility=Visibility.NONE, setterVisibility=Visibility.NONE)
public class TimeModel {
	private static final int MASK_SIZE = 10;
	private static final double MASK_EXPONENT = 1.25;
	
	private ArrayList<Double> buckets;
	private double norm;
	
	private int timeResolution;
	private int bucketSize;
	private int numOfBuckets;
	
	
	private FeatureCalibration calibration;
	
	public TimeModel(int timeResolution, int bucketSize){
		this.timeResolution = timeResolution;
		this.bucketSize = bucketSize;
		this.numOfBuckets = (int) Math.ceil(timeResolution/(double)bucketSize);
		init();
	}
	
	public void init(){
		buckets = new ArrayList<>(numOfBuckets);
		for(int i = 0; i < numOfBuckets; i++){
			buckets.add(0D);
		}
		setNorm(0);
	}
	
	public double score(long epochSeconds){
		Integer bucketName = new Integer(getBucketIndex(epochSeconds));
		
		return calibration != null ? calibration.score(bucketName) : 0;
	}
	
	public int getBucketIndex(long epochSeconds){
		return (int) ( (epochSeconds % timeResolution) / bucketSize );
	}
			
	public void update(long epochSeconds) throws Exception{
		int pivot = getBucketIndex(epochSeconds);
		buckets.set(pivot, buckets.get(pivot)+1);
		int upIndex = pivot + 1;
		int downIndex = pivot - 1 + numOfBuckets;
		for(int i = 0; i < MASK_SIZE; i++,upIndex++,downIndex--){
			double addVal = Math.pow(MASK_EXPONENT, 0-i);
			buckets.set(upIndex % numOfBuckets, buckets.get(upIndex % numOfBuckets) + addVal);
			buckets.set(downIndex % numOfBuckets, buckets.get(downIndex % numOfBuckets) + addVal);
		}
		
		norm++;
		
		updateCalibration(pivot, buckets.get(pivot));
	}
	
	private void updateCalibration(int bucketIndex, double val) throws Exception{
		if(calibration == null){
			initCalibration();
		} else{
			calibration.updateFeatureValueCount(new Integer(bucketIndex), val);
		}
	}
	
	private void initCalibration() throws Exception{
		calibration = new FeatureCalibration(FeatureCalibrationBucketScorer.class);
		Map<Object, Double> tmp = new HashMap<>();
		
		for(int i = 0; i < numOfBuckets; i++){
			Double val = buckets.get(i);
			if(val < 1){
				continue;
			}
			
			tmp.put(new Integer(i), val);
		}
		
		calibration.init(tmp);
	}

	public double getNorm() {
		return norm;
	}

	public void setNorm(double norm) {
		this.norm = norm;
	}

	public int getTimeResolution() {
		return timeResolution;
	}

	public int getBucketSize() {
		return bucketSize;
	}

	public int getNumOfBuckets() {
		return numOfBuckets;
	}
	
	
}
