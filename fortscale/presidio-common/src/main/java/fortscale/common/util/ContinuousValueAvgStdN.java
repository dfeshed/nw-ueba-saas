package fortscale.common.util;

import fortscale.common.feature.FeatureValue;

import java.io.Serializable;

/**
 * Created by amira on 17/06/2015.
 */
public class ContinuousValueAvgStdN implements Serializable, FeatureValue{
    private static final long serialVersionUID = 1L;
    public static final String FEATURE_VALUE_TYPE = "continuous_value_avg_std_n";

    private Long N = 0L;
    private Double std = 0.0;
    private Double avg = 0.0;
    private Double sigma_x_pwr_2_div_n = 0.0; // Sigma(x^2) / N

    synchronized public ContinuousValueAvgStdN add(Double value) {
        if(value==null) return this;
        Double new_avg = (avg*N+value) / (N+1);
        Double new_sigma_x_pwr_2 = (sigma_x_pwr_2_div_n * N)+Math.pow(value, 2);
        Double old_sigma_x = avg * N;
        Double new_sigma_x = old_sigma_x + value;
        Double A = 2*new_avg*new_sigma_x;
        Double B = (N+1)*Math.pow(new_avg,2);
        Double new_std = Math.sqrt((new_sigma_x_pwr_2-A+B)/(N+1));

        N+=1;
        avg = new_avg;
        std = new_std;
        sigma_x_pwr_2_div_n = new_sigma_x_pwr_2 / N;
        return this;
    }

    synchronized public ContinuousValueAvgStdN add(ContinuousValueAvgStdN asn) {
        Long new_N = N+asn.N;
        Double new_sigma_x = avg*N + asn.avg*asn.N;
        Double new_avg = new_sigma_x / new_N;
        Double A = 2*new_avg*new_sigma_x;
        Double B = (new_N)*Math.pow(new_avg,2);
        Double new_sigma_x_pwr_2 = (sigma_x_pwr_2_div_n * N)+(asn.sigma_x_pwr_2_div_n * asn.N);
        Double new_std = Math.sqrt((new_sigma_x_pwr_2-A+B)/new_N);

        N = new_N;
        std = new_std;
        avg = new_avg;
        sigma_x_pwr_2_div_n = new_sigma_x_pwr_2 / N;

        return this;
    }

    public Long getN() {
        return N;
    }

    public Double getStd() {
        return std;
    }

    public Double getAvg() {
        return avg;
    }
}
