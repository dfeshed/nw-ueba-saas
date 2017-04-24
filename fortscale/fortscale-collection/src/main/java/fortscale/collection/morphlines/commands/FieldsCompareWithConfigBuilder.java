package fortscale.collection.morphlines.commands;

import com.typesafe.config.Config;
import fortscale.collection.configuration.CollectionPropertiesResolver;
import org.kitesdk.morphline.api.Command;
import org.kitesdk.morphline.api.CommandBuilder;
import org.kitesdk.morphline.api.MorphlineContext;
import org.kitesdk.morphline.api.Record;
import org.kitesdk.morphline.base.AbstractCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * This Command does a compare action on given fields from the record with some value from configuration.
 * Supports comparison of configuration as a comma-separated-list (will check if the fields is equal/contains/etc to at least one of the values in the list)
 * Valid comparison types are: equals, contains. default comparison type is: equals
 */
public class FieldsCompareWithConfigBuilder implements CommandBuilder {

    private static Logger logger = LoggerFactory.getLogger(FieldsCompareWithConfigBuilder.class);

    @Override
    public Collection<String> getNames() {
        return Collections.singletonList("FieldsCompareWithConfig");
    }

    @Override
    public Command build(Config config, Command parent, Command child, MorphlineContext context) {
        return new FieldsCompareWithConfigBuilder.FieldsCompareWithConfig(this, config, parent, child, context);
    }

    // /////////////////////////////////////////////////////////////////////////////
    // Nested classes:
    // /////////////////////////////////////////////////////////////////////////////
    @Configurable(preConstruction = true)
    public static final class FieldsCompareWithConfig extends AbstractCommand {

        @Autowired
        private CollectionPropertiesResolver collectionPropertiesResolver;

        private String field;
        private String configuration;
        private String comparisonType;



        public FieldsCompareWithConfig(CommandBuilder builder, Config config, Command parent, Command child, MorphlineContext context) {
            super(builder, config, parent, child, context);
            field = getConfigs().getString(config, "field");
            configuration = getConfigs().getString(config, "configuration");
            comparisonType = getConfigs().getString(config, "comparisonType", "equals");
        }

        @Override
        protected boolean doProcess(Record inputRecord) {
            final String fieldValue = (String)inputRecord.getFirstValue(field);
            final String configValue = getProperty(configuration); // this can be a CSV list as a single string
            if (configValue == null) {
                logger.error("Tried to get configuration {} but it does not exist");
                return false;
            }
            final List<String> valuesToCompare = Arrays.asList(configValue.split(","));
            switch(comparisonType) {
                case "equals": {
                    for (String valueToCompare : valuesToCompare) {
                        if (fieldValue.equals(valueToCompare)) {
                            return true;
                        }
                    }
                    break;
                }
                case "contains":
                    for (String valueToCompare : valuesToCompare) {
                        if (fieldValue.contains(valueToCompare)) {
                            return true;
                        }
                    }
                    break;
                default:
                    logger.error("Invalid comparisonType - {}. Valid compare types are: equals,contains");
                    return false;
            }


            return false;
        }

        private String getProperty(String propertyKey) {
            String propertyValue = null;
            try {
                propertyValue = collectionPropertiesResolver.getPropertyValue(propertyKey);
            } catch (Exception e) {
                logger.error("Property {} doesn't exist.", propertyKey);
            }

            return propertyValue;
        }

    }
}
