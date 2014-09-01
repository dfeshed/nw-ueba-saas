package fortscale.collection.morphlines.commands;

import com.typesafe.config.Config;
import org.kitesdk.morphline.api.Command;
import org.kitesdk.morphline.api.CommandBuilder;
import org.kitesdk.morphline.api.MorphlineContext;
import org.kitesdk.morphline.api.Record;
import org.kitesdk.morphline.base.AbstractCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.core.env.Environment;

import java.util.Collection;
import java.util.Collections;

/**
 * Created by idanp on 9/1/2014.
 * This command will parse the user name from a string based on character that will represent the start index and character that will represent the end index
 */
public final class ParseFieldBuilder implements CommandBuilder {

    @Override
    public Collection<String> getNames() {
        return Collections.singletonList("ParseUserName");
    }

    @Override
    public Command build(Config config, Command parent, Command child, MorphlineContext context) {
        return new ParseField(this, config, parent, child, context);
    }

    // /////////////////////////////////////////////////////////////////////////////
    // Nested classes:
    // /////////////////////////////////////////////////////////////////////////////
    @Configurable(preConstruction=true)
    public static final class ParseField extends AbstractCommand {

        private String leftSignCharacter;
        private String rightSignCharacter;
        private String fieldName;
        private String outputField;
        private Boolean ignoreConfig;
        private String configurationPath;
        private boolean toParse;


        @Autowired
        Environment env;



        public ParseField(CommandBuilder builder, Config config, Command parent, Command child, MorphlineContext context) {
            super(builder, config, parent, child, context);
            leftSignCharacter = getConfigs().getString(config, "leftSignCharacter");
            rightSignCharacter = getConfigs().getString(config, "rightSignCharacter");
            fieldName = getConfigs().getString(config, "fieldName");
            outputField = getConfigs().getString(config, "outputField");
            ignoreConfig = getConfig().getBoolean("ignoreConfig");
            configurationPath = getConfigs().getString(config, "configurationPath");
            toParse = Boolean.getBoolean(env.getProperty(configurationPath));



        }

        @Override
        protected boolean doProcess(Record inputRecord) {


            if ((!ignoreConfig && toParse) || ignoreConfig) {

                String fieldContent = (String) inputRecord.getFirstValue(fieldName);
                int leftIndex = leftSignCharacter == null ? 0 : fieldContent.indexOf(leftSignCharacter);
                int rightIndex = rightSignCharacter == null ? fieldContent.length() : fieldContent.indexOf(rightSignCharacter);
                String newFIeldContent = fieldContent.substring(leftIndex, rightIndex);

                if (outputField == null)
                    inputRecord.replaceValues(fieldName, newFIeldContent);
                else
                    inputRecord.put(outputField, newFIeldContent);


            }

            return super.doProcess(inputRecord);

        }


    }

}
