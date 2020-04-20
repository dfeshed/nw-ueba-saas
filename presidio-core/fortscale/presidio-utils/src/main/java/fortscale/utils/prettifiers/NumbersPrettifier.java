package fortscale.utils.prettifiers;

import java.text.DecimalFormat;

/**
 * Created by avivs on 20/01/16.
 */
public class NumbersPrettifier {

    /**
     * Returns just the natural number if decimals are zero
     *
     * @return
     */
    public static String truncateDecimalsOnNatural (double number) {
        if (number - Math.floor(number) == 0) {
            DecimalFormat df = new DecimalFormat("#");
            return df.format(number);
        }

        return String.valueOf(number);
    }

    /**
     * Returns just the natural number if decimals are zero
     *
     * @return
     */
    public static String truncateDecimalsOnNatural (String numberStr) {
        double number;
        try {
            number = Double.parseDouble(numberStr);
        } catch (NumberFormatException e) {
            return numberStr;
        }

        return truncateDecimalsOnNatural(number);
    }

}
