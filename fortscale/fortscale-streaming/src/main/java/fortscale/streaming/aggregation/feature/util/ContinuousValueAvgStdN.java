package fortscale.streaming.aggregation.feature.util;

import fortscale.streaming.serialization.LongSerde;

import java.io.Serializable;

/**
 * Created by amira on 17/06/2015.
 */
public class ContinuousValueAvgStdN implements Serializable{
    private static final long serialVersionUID = 1L;

    private Long N = 0L;
    private Double std = 0.0;
    private Double avg = 0.0;
    private Double sigma_x_pwr_2_div_n = 0.0; // Sigma(x^2) / N

    synchronized public void add(Double value) {
        if(value==null) return;;
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
