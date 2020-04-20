package fortscale.utils.transform.stringformat;

import com.google.common.base.CaseFormat;
import org.apache.commons.lang3.Validate;

public enum StringFormat {
    // this_is_an_example
    LOWER_UNDERSCORE(CaseFormat.LOWER_UNDERSCORE) {
        @Override
        String doConvert(StringFormat targetStringFormat, String string) {
            if (targetStringFormat == UPPER_HYPHEN) return string.toUpperCase().replace('_', '-');
            else if (targetStringFormat == LOWER_SPACE) return string.replace('_', ' ');
            // Otherwise, targetStringFormat == UPPER_SPACE
            else return string.toUpperCase().replace('_', ' ');
        }
    },

    // THIS_IS_AN_EXAMPLE
    UPPER_UNDERSCORE(CaseFormat.UPPER_UNDERSCORE) {
        @Override
        String doConvert(StringFormat targetStringFormat, String string) {
            return LOWER_UNDERSCORE.convert(targetStringFormat, string.toLowerCase());
        }
    },

    // thisIsAnExample
    LOWER_CAMEL(CaseFormat.LOWER_CAMEL) {
        @Override
        String doConvert(StringFormat targetStringFormat, String string) {
            string = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, string);
            return LOWER_UNDERSCORE.convert(targetStringFormat, string);
        }
    },

    // ThisIsAnExample
    UPPER_CAMEL(CaseFormat.UPPER_CAMEL) {
        @Override
        String doConvert(StringFormat targetStringFormat, String string) {
            string = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, string);
            return LOWER_UNDERSCORE.convert(targetStringFormat, string);
        }
    },

    // this-is-an-example
    LOWER_HYPHEN(CaseFormat.LOWER_HYPHEN) {
        @Override
        String doConvert(StringFormat targetStringFormat, String string) {
            return LOWER_UNDERSCORE.convert(targetStringFormat, string.replace('-', '_'));
        }
    },

    // THIS-IS-AN-EXAMPLE
    UPPER_HYPHEN(null) {
        @Override
        String doConvert(StringFormat targetStringFormat, String string) {
            return LOWER_UNDERSCORE.convert(targetStringFormat, string.toLowerCase().replace('-', '_'));
        }
    },

    // this is an example
    LOWER_SPACE(null) {
        @Override
        String doConvert(StringFormat targetStringFormat, String string) {
            return LOWER_UNDERSCORE.convert(targetStringFormat, string.replace(' ', '_'));
        }
    },

    // THIS IS AN EXAMPLE
    UPPER_SPACE(null) {
        @Override
        String doConvert(StringFormat targetStringFormat, String string) {
            return LOWER_UNDERSCORE.convert(targetStringFormat, string.toLowerCase().replace(' ', '_'));
        }
    };

    // The equivalent Google CaseFormat for this StringFormat, or null if there is none
    private final CaseFormat caseFormat;

    StringFormat(CaseFormat caseFormat) {
        this.caseFormat = caseFormat;
    }

    public String convert(StringFormat targetStringFormat, String string) {
        Validate.notNull(targetStringFormat, "targetStringFormat cannot be null.");
        Validate.notNull(string, "string cannot be null.");

        if (targetStringFormat == this)
            return string;
        else if (caseFormat == null || targetStringFormat.caseFormat == null)
            return doConvert(targetStringFormat, string);
        else
            return caseFormat.to(targetStringFormat.caseFormat, string);
    }

    abstract String doConvert(StringFormat targetStringFormat, String string);
}
