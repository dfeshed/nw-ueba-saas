package com.rsa.netwitness.presidio.automation.common.helpers;

import com.google.common.base.CaseFormat;

import java.util.ArrayList;
import java.util.List;

public class NamesConversionUtils {
    public static String revertProcessOperationType(String optype) {
        // Operation type should be in LOWER_CAMEL
        if (optype == null) return null;
        return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, optype);
    }

    public static List<String> revertDirGroups(List<String> dirGroups) {
        // Directory groups should be in LOWER_CAMEL
        List<String> converted = new ArrayList<>();
        if (dirGroups != null) {
            for (String group : dirGroups) {
                if (group == null) {
                    converted.add(null);
                } else {
                    converted.add(CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, group));
                }
            }
        }

        return converted;
    }
    public static List<String> revertCategories(List<String> categoryInAdeFormat) {
        // Categories should be in LOWER_SPACE
        if (categoryInAdeFormat == null) return null;
        List<String> converted = new ArrayList<>();
        for (String category : categoryInAdeFormat) {
            if (category == null) {
                converted.add(null);
            } else {
                converted.add(CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_UNDERSCORE, category).replace("_", " "));
            }
        }

        return converted;
    }

    public static String revert2LowerCamel(String optype) {
        // Operation type should be in LOWER_CAMEL
        if (optype == null) return null;
        return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, optype);
    }

    public static  String fixRegistryOperation(String operation) {
        // workaround until generator fix will be merged to master

        return (operation.equalsIgnoreCase("MODIFY_REGISTRY_KEY"))?"MODIFY_REGISTRY_VALUE":operation;
    }

    public static  String fixMachineName(String machineName) {
        // workaround until generator of machine / scenario will be fixed
        if (machineName == null || machineName.length() == 0) {
            System.out.println("missing machine name");
            return "dummy";
        }
        return (machineName.replace(" ", "_"));
    }


}
