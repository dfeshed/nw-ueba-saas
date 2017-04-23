package fortscale.collection.morphlines.commands;

import com.typesafe.config.Config;
import fortscale.collection.configuration.CollectionPropertiesResolver;
import fortscale.utils.properties.IllegalStructuredProperty;
import fortscale.utils.properties.PropertyNotExistException;
import org.kitesdk.morphline.api.Command;
import org.kitesdk.morphline.api.CommandBuilder;
import org.kitesdk.morphline.api.MorphlineContext;
import org.kitesdk.morphline.api.Record;
import org.kitesdk.morphline.base.AbstractCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * This Command does a compare action on given fields from the record with some value from configuration.
 * Supports comparison of lists of fields & config-values (according to order - first in fields-list will be compared with first from configs-list and so on...)
 * Valid comparison types are: equals, contains (please notice that the contains is the normal String contains (not like the morphline command contains)). default comparison type is: equals
 */
public class FieldsCompareWithConfigBuilder implements CommandBuilder {

    private static Logger logger = LoggerFactory.getLogger(FieldsCompareWithConfigBuilder.class);

    @Autowired
    private CollectionPropertiesResolver collectionPropertiesResolver;

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
    public final class FieldsCompareWithConfig extends AbstractCommand {
        private List<String> fields;
        private List<String> configs;
        private String comparisonType;



        public FieldsCompareWithConfig(CommandBuilder builder, Config config, Command parent, Command child, MorphlineContext context) {
            super(builder, config, parent, child, context);
            fields = getConfigs().getStringList(config, "fields");
            configs = getConfigs().getStringList(config, "configs");
            comparisonType = getConfigs().getString(config, "comparisonType", "equals");
        }

        @Override
        protected boolean doProcess(Record inputRecord) {
            for (int i = 0; i < fields.size(); i++) {
                final String currFieldKey = fields.get(i);
                final String currConfigKey = configs.get(i);

                final String currFieldValue = (String)inputRecord.getFirstValue(currFieldKey);
                final String currConfigValue;
                try {
                    currConfigValue = getProperty(currConfigKey);
                } catch (IllegalStructuredProperty | PropertyNotExistException e) {
                    logger.error("Property {} doesn't exist.", currConfigKey);
                    return false;
                }

                switch(comparisonType) {
                    case "equals": {
                        if(!currFieldValue.equals(currConfigValue)) {
                            return false;
                        }
                        break;
                    }
                    case "contains":
                        if(!currFieldValue.contains(currConfigValue)) {
                            return false;
                        }
                        break;
                    default:
                        logger.error("Invalid comparisonType - {}. Valid compare types are: equals,contains");
                        return false;
                }
            }


            return true;
        }

        private String getProperty(String propertyKey) throws IllegalStructuredProperty, PropertyNotExistException {
            return collectionPropertiesResolver.getEnvPropertyValue(propertyKey);
        }

    }
}
