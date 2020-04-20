package fortscale.ml.model;

public class Sigmoid {
	/**
	 * Apply a logistic function on the given input.
	 * A logistic function behaves approximately like this:
	 *                      |
	 *                   1 -|......
	 *                      |       .....
	 *                      |             ...
	 *                      |                 ..
	 *                      |                    .
	 *                      |                     .
	 *                      |                      .
	 *                      |                       .
	 *                 0.5 -|                       .
	 *                      |                        .
	 *                      |                         .
	 *                      |                          .
	 *                      |                            ...
	 *                      |                                .....
	 * epsilonValueForMaxX -|                                      ........
	 *                     _|______________________________________________
	 *                      |                      |                      |
	 *                                       xWithValueHalf             maxX
	 *
	 * For more info, look into
	 * 		https://www.google.co.il/search?q=1%2F(1%2B(x%2B1.5)%5E4.18)&oq=1%2F(1%2B(x%2B1.5)%5E4.18)&aqs=chrome..69i57j69i59l2.239j0j7&sourceid=chrome&es_sm=0&ie=UTF-8#q=1%2F(1%2Bx%5E4.182667533025268)
	 *
	 * @param xWithValueHalf the x value which should get a value of 0.5.
	 * @param maxX the x value which should get an epsilon value.
	 * @param epsilonValueForMaxX the epsilon value maxX should get
	 * @param x the function input.
	 */
	public static double calcLogisticFunc(double xWithValueHalf, double maxX, double epsilonValueForMaxX, double x) {
		double logisticFunctionDomain = maxX / xWithValueHalf;
		double steepness = Math.log((1 / epsilonValueForMaxX) - 1) / Math.log(logisticFunctionDomain);
//		double steepness = Math.log(1 / epsilonValueForMaxX) / Math.log(logisticFunctionDomain);
		return 1 / (1 + Math.pow(x * logisticFunctionDomain / maxX, steepness));
	}
}
