package fortscale.collection.morphlines.commands;

import com.typesafe.config.Config;
import fortscale.collection.monitoring.CollectionMessages;
import fortscale.collection.monitoring.MorphlineCommandMonitoringHelper;
import fortscale.collection.morphlines.ExactMatcherFileList;
import fortscale.collection.morphlines.MatcherFileList;
import fortscale.collection.morphlines.RegexMatcherFileList;
import fortscale.collection.morphlines.metrics.MorphlineMetrics;
import fortscale.utils.logging.Logger;
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

import java.util.Collection;
import java.util.Collections;

public class FieldFilterCmdBuilder implements CommandBuilder{
    private static Logger logger = Logger.getLogger(FieldFilterCmdBuilder.class);
	
	@Override
    public Collection<String> getNames() {
        return Collections.singletonList("FieldFilter");
    }

    @Override
    public Command build(Config config, Command parent, Command child, MorphlineContext context) {
        return new FieldFilter(this, config, parent, child, context);
    }

    // /////////////////////////////////////////////////////////////////////////////
    // Nested classes:
    // /////////////////////////////////////////////////////////////////////////////
    @Configurable(preConstruction=true)
    public static final class FieldFilter extends AbstractCommand implements EmbeddedValueResolverAware, ApplicationContextAware {
    	
    	private final String renderedConfig; // cached value

        private MatcherFileList matcherFileList;
        private String fieldName;
        private boolean isBlacklist;
        
        StringValueResolver stringValueResolver;
        ApplicationContext applicationContext;

        MorphlineCommandMonitoringHelper commandMonitoringHelper = new MorphlineCommandMonitoringHelper();


        public FieldFilter(CommandBuilder builder, Config config, Command parent, Command child, MorphlineContext context) {
            super(builder, config, parent, child, context);
            fieldName = getConfigs().getString(config, "fieldName");
            fieldName = stringValueResolver.resolveStringValue(fieldName);
            
            String listFileStr = getConfigs().getString(config, "listFile");
            Resource listResource = applicationContext.getResource(listFileStr);
            boolean isRegex = getConfigs().getBoolean(config, "isRegex", true);
            if(isRegex){
            	matcherFileList = new RegexMatcherFileList(listResource);
            } else{
            	matcherFileList = new ExactMatcherFileList(listResource);
            }
            
            isBlacklist = getConfigs().getBoolean(config, "isBlacklist", true);
            
            this.renderedConfig = config.root().render();
        }

        @Override
        protected boolean doProcess(Record inputRecord) {

			//The specific Morphline metric
			MorphlineMetrics morphlineMetrics = commandMonitoringHelper.getItemContext(inputRecord).getMorphlineMetrics();

        	String fieldContent = (String) inputRecord.getFirstValue(fieldName);
        	
        	boolean isMatch = matcherFileList.isMatch(fieldContent);
            if((isBlacklist && isMatch) || (!isBlacklist && !isMatch) ){
            	// drop record
				logger.debug("FieldFilter command droped record because {} is in the black list of field {}. command: {}, record: {}", fieldContent, fieldName, renderedConfig, inputRecord.toString());
                commandMonitoringHelper.addFilteredEventToMonitoring(inputRecord,
                        CollectionMessages.BLACK_LIST_FILTER, fieldContent, fieldName);
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

