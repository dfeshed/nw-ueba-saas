package fortscale.ml.model.prevalance.field;

public class QuadPolyCalibrationForContModel {

	private double a2;
	private double a1;
	private double largestPValue;
	private boolean isScoreForLargeValues;
	private boolean isScoreForSmallValues;
	
	public QuadPolyCalibrationForContModel(double a2, double a1, double largestPValue, boolean isScoreForLargeValues, boolean isScoreForSmallValues){
		this.a1 = a1;
		this.a2 = a2;
		this.largestPValue = largestPValue;
		this.isScoreForLargeValues = isScoreForLargeValues;
		this.isScoreForSmallValues = isScoreForSmallValues;
	}
	
	public double calculateScore(double modelScore){
		double score = 0;

		double p = Math.abs(modelScore) - ContinuousValuesModel.SEPARATOR_BETWEEN_SMALL_AND_LARGE_VALUE_DENSITY;
		
		if(p < largestPValue && 
				((modelScore > 0 && isScoreForLargeValues) || (modelScore < 0 && isScoreForSmallValues)) ){
			score = Math.max(a2*Math.pow(p, 2) - a1*p + 1, 0);
			score = Math.round(score*100);
		}
		
		return score;
	}
}
