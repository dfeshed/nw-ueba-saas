package fortscale.utils.actdir;

public class LogsToADConversions {
    public static String getHostShortName(String input) {
        int firstDotIndex = input.indexOf('.') ;
        return (firstDotIndex > 0) ? input.substring(0, firstDotIndex) : input; 
    }
}
