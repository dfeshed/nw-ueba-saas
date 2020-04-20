package fortscale.ml.model;


public interface IContinuousDataModel extends Model{

	/**
	 *
	 * @return standard deviation
	 */
	double getSd();

	long getN();

	double getMean();

	double getMaxValue();
}
