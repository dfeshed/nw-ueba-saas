package fortscale.utils.logging.annotation;

import java.util.function.Function;

/**
 * Created by shays on 05/07/2016.
 * Annotation can't contain functions as memebers,
 * so to work arround it I have created this enum which contains the
 * function as enum
 */

public enum LogSensitiveFunctionsAsEnum {
    APPLICATION_CONFIGURATION(LogSensitiveFunctionsAsEnum::applicationConfigurationIsSensitiveDecider),
    ALWAYS_SENSITIVE((argumentsArray -> {return true;})),
    NEVER_SENSITIVE((argumentsArray -> {return false;}));

    private Function<Object[],Boolean> isSensitiveFunction;

    LogSensitiveFunctionsAsEnum(Function<Object[], Boolean> f){
        this.isSensitiveFunction = f;
    }

    public Function<Object[],Boolean> getIsSensitiveFunction() {
        return isSensitiveFunction;
    }

    //This method check if the application configuration contain sensitive content
    private static boolean applicationConfigurationIsSensitiveDecider(Object[] arguments){
        if (arguments[0] instanceof String && ((String) arguments[0]).contains("\"encrypt\":true")){
            return true;
        }
        return  false;
    }


}
