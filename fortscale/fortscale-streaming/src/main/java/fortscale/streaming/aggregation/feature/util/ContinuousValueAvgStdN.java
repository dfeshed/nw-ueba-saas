package fortscale.streaming.aggregation.feature.util;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ContinuousValueAvgStdN avgStdN = (ContinuousValueAvgStdN) o;

        if (!N.equals(avgStdN.N)) return false;
        if (Math.abs(std-avgStdN.std)>0.0000000001) return false;
        if (Math.abs(avg -avgStdN.avg)>0.0000000001) return false;
        return Math.abs(sigma_x_pwr_2_div_n-avgStdN.sigma_x_pwr_2_div_n)<0.0000000001;

    }

    @Override
    public int hashCode() {
        int result = N.hashCode();
        result = 31 * result + std.hashCode();
        result = 31 * result + avg.hashCode();
        result = 31 * result + sigma_x_pwr_2_div_n.hashCode();
        return result;
    }
}
