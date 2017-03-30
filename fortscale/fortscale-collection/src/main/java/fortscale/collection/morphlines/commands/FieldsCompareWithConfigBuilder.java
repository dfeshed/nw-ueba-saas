package fortscale.collection.morphlines.commands;

import com.typesafe.config.Config;
import fortscale.collection.configuration.CollectionPropertiesResolver;
import fortscale.collection.morphlines.MorphlineConfigService;
import fortscale.utils.properties.IllegalStructuredProperty;
import fortscale.utils.properties.PropertyNotExistException;
import fortscale.utils.spring.SpringPropertiesUtil;
import org.kitesdk.morphline.api.Command;
import org.kitesdk.morphline.api.CommandBuilder;
import org.kitesdk.morphline.api.MorphlineContext;
import org.kitesdk.morphline.api.Record;
import org.kitesdk.morphline.base.AbstractCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySources;

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
    private PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer;

    @Autowired
    private MorphlineConfigService morphlineConfigService;

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
        private String compareType;



        public FieldsCompareWithConfig(CommandBuilder builder, Config config, Command parent, Command child, MorphlineContext context) {
            super(builder, config, parent, child, context);
            fields = getConfigs().getStringList(config, "fields");
            configs = getConfigs().getStringList(config, "configs");
            compareType = getConfigs().getString(config, "compareType", "equals");
        }

        @Override
        protected boolean doProcess(Record inputRecord) {
            boolean ans = true;
            switch(compareType) {
                case "equals":
                    for (int i = 0; i < fields.size(); i++) {
                        final String currFieldKey = fields.get(i);
                        final String currConfigKey = configs.get(i);

                        final String currFieldValue = (String)inputRecord.getFirstValue(currFieldKey);
                        final String currConfigValue = getProperty(currConfigKey);
                        
                        ans = currFieldValue.equals(currConfigValue);
                    }
                    break;
                case "contains":
                    for (int i = 0; i < fields.size(); i++) {
                        final String currFieldKey = fields.get(i);
                        final String currConfigKey = configs.get(i);

                        final String currFieldValue = (String)inputRecord.getFirstValue(currFieldKey);
                        final String currConfigValue = getProperty(currConfigKey);

                        ans = currFieldValue.contains(currConfigValue);
                    }
                    break;
                default:
                    logger.error("Invalid compareType - {}. Valid compare types are: equals,contains");
                    break;
            }

            return ans;
        }

        private String getProperty(String property) {
            return SpringPropertiesUtil.getProperty(property);
        }

    }
}
