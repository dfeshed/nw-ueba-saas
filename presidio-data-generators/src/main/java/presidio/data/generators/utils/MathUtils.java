package presidio.data.generators.utils;

public class MathUtils {
    // calculates greatest common denominator on two integers
    public static int gcd(int x, int y)
    {
        return y == 0 ? x : gcd(y,x%y);
    }

    // calculates GCD on list of integers
    public static int gcd(int[] input)
    {
        int result = input[0];
        for(int i = 1; i < input.length; i++) result = gcd(result, input[i]);
        return result;
    }
}
