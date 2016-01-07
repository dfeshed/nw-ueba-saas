package fortscale.ml.model.prevalance.field;

public class QuadPolyCalibrationForContModel {

	private double a2;
	private double a1;
	private double sensitivity;
	private double largestPValue;
	private boolean isScoreForLargeValues;
	private boolean isScoreForSmallValues;
	
	public QuadPolyCalibrationForContModel(double a2, double a1, double sensitivity, boolean isScoreForLargeValues, boolean isScoreForSmallValues){
		this.a1 = a1;
		this.a2 = a2;
		this.sensitivity = sensitivity;
		this.largestPValue = calcQuadraticLeftIntersection(a2, a1, sensitivity);
		this.isScoreForLargeValues = isScoreForLargeValues;
		this.isScoreForSmallValues = isScoreForSmallValues;
	}

	private double calcQuadraticLeftIntersection(double a2, double a1, double sensitivity) {
		// use the quadratic formula
		return (a1 * sensitivity - Math.sqrt(Math.pow(a1 * sensitivity, 2) - 4 * a2 * Math.pow(sensitivity, 2))) / (2 * a2 * Math.pow(sensitivity, 2));
	}

	public double calculateScore(double modelScore){
		double score = 0;

		double p = Math.abs(modelScore) - ContinuousValuesModel.SEPARATOR_BETWEEN_SMALL_AND_LARGE_VALUE_DENSITY;

		p = Math.min(p*sensitivity, 1);
		if(p < largestPValue &&
				((modelScore > 0 && isScoreForLargeValues) || (modelScore < 0 && isScoreForSmallValues)) ){
			score = Math.max(a2*Math.pow(p, 2) - a1*p + 1, 0);
			score = Math.round(score*100);
		}
		
		return score;
	}
}
