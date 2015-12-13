package fortscale.services.classifier;

import fortscale.services.fe.Classifier;
import org.apache.commons.lang3.EnumUtils;

/**
 * @author gils
 * Date: 13/12/2015
 */
public class ClassifierHelper {

    public static String getLogEventName(String classifierId) {
        boolean classifierExist = EnumUtils.isValidEnum(Classifier.class, classifierId);

        if (classifierExist) {
            Classifier classifierType = Classifier.valueOf(classifierId);

            return classifierType.getLogEventsEnum().getId();
        }

        return classifierId;
    }

    public static String getUserApplication(String classifierId) {
        boolean classifierExist = EnumUtils.isValidEnum(Classifier.class, classifierId);

        if (classifierExist) {
            Classifier classifierType = Classifier.valueOf(classifierId);

            return classifierType.getUserApplication().getId();
        }

        return classifierId;
    }

    public static String getClassifierDisplayName(String classifierId) {
        return classifierId.toUpperCase();
    }
}
