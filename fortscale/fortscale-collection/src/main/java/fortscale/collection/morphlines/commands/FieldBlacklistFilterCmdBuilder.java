package fortscale.collection.morphlines.commands;

import java.util.Collection;
import java.util.Collections;

import org.kitesdk.morphline.api.Command;
import org.kitesdk.morphline.api.CommandBuilder;
import org.kitesdk.morphline.api.MorphlineContext;
import org.kitesdk.morphline.api.Record;
import org.kitesdk.morphline.base.AbstractCommand;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.core.io.Resource;
import org.springframework.util.StringValueResolver;

import com.typesafe.config.Config;

import fortscale.collection.morphlines.RegexFileList;
import fortscale.collection.morphlines.commands.ParseFieldBuilder.ParseField;
import fortscale.utils.logging.Logger;

public class FieldBlacklistFilterCmdBuilder implements CommandBuilder{
	private static Logger logger = Logger.getLogger(FieldBlacklistFilterCmdBuilder.class);
	
	@Override
    public Collection<String> getNames() {
        return Collections.singletonList("FieldBlacklistFilter");
    }

    @Override
    public Command build(Config config, Command parent, Command child, MorphlineContext context) {
        return new ParseField(this, config, parent, child, context);
    }

    // /////////////////////////////////////////////////////////////////////////////
    // Nested classes:
    // /////////////////////////////////////////////////////////////////////////////
    @Configurable(preConstruction=true)
    public static final class FieldBlacklistFilter extends AbstractCommand implements EmbeddedValueResolverAware, ApplicationContextAware {
    	
    	private final String renderedConfig; // cached value

        private RegexFileList regexFileList;
        private String fieldName;
        
        StringValueResolver stringValueResolver;
        ApplicationContext applicationContext;



        public FieldBlacklistFilter(CommandBuilder builder, Config config, Command parent, Command child, MorphlineContext context) {
            super(builder, config, parent, child, context);
            fieldName = getConfigs().getString(config, "fieldName");
            fieldName = stringValueResolver.resolveStringValue(fieldName);
            String blacklistFileStr = getConfigs().getString(config, "blacklistFile");
            Resource blacklistResource = applicationContext.getResource(blacklistFileStr);
            regexFileList = new RegexFileList(blacklistResource);
            
            this.renderedConfig = config.root().render();
        }

        @Override
        protected boolean doProcess(Record inputRecord) {
        	String fieldContent = (String) inputRecord.getFirstValue(fieldName);

            if(regexFileList.isMatch(fieldContent)){
            	// drop record
				logger.debug("FieldBlacklistFilter command droped record because {} is in the black list of field {}. command: {}, record: {}", fieldContent, fieldName, renderedConfig, inputRecord.toString());
				return true;
            }

            return super.doProcess(inputRecord);

        }



		@Override
		public void setEmbeddedValueResolver(StringValueResolver resolver) {
			this.stringValueResolver = resolver;
		}

		@Override
		public void setApplicationContext(ApplicationContext applicationContext)
				throws BeansException {
			this.applicationContext = applicationContext;
		}
    }

}

