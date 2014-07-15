package fortscale.collection.morphlines.commands;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.kitesdk.morphline.api.Command;
import org.kitesdk.morphline.api.CommandBuilder;
import org.kitesdk.morphline.api.MorphlineContext;
import org.kitesdk.morphline.api.Record;
import org.kitesdk.morphline.base.AbstractCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;


import com.google.common.cache.LoadingCache;
import com.typesafe.config.Config;

import fortscale.services.FilterMachinesService;

public class OUMachineFilterCmdBuilder implements CommandBuilder {

    @Override
    public Command build(Config config, Command parent, Command child,
            MorphlineContext context) {
        return new FilterOUMachine(this, config, parent, child, context);
    }

    @Override
    public Collection<String> getNames() {
        return Collections.singletonList("FilterOUMachine");
    }

    // /////////////////////////////////////////////////////////////////////////////
    // Nested classes:
    // /////////////////////////////////////////////////////////////////////////////
    @Configurable(preConstruction = true)
    public static class FilterOUMachine extends AbstractCommand {
    	private static Logger logger = LoggerFactory
    			.getLogger(FilterOUMachine.class);
		@Autowired
		private FilterMachinesService service;
        private String hostnameField;
        private String regex;
        private Pattern regexMatcher;
        private String regexReplacement;
        LoadingCache<String, Boolean> OUMachinesCache;
        
        
        HashMap<String, Boolean> OUmachines = null;
        public FilterOUMachine(CommandBuilder builder, Config config,
                Command parent, Command child, MorphlineContext context) {
            super(builder, config, parent, child, context);

            this.hostnameField = getConfigs().getString(config,
                    "hostnameField");
            this.regex = getConfigs().getString(config, "regex", null);
            if(regex != null){
            	String[] regexArray = regex.split("# #");
            	//TODO: add check for array size
            	regexMatcher = Pattern.compile(regexArray[0]);
            	regexReplacement = regexArray[1];
            }

        }
        
        
        @Override
        protected boolean doProcess(Record inputRecord) {
        	// get the machine_name from the record
        	String computerName = (String) inputRecord
                    .getFirstValue(this.hostnameField);
        	if(regex != null){
        		Matcher m = regexMatcher.matcher(computerName);
        		if(m.matches()){
        			computerName = m.replaceAll(regexReplacement);
        		}else{
        			logger.error("could not match hostname to the regex {} : {}",regex, computerName);
        			return true;
        		}
        	} 
        	computerName = computerName.toUpperCase();
        	boolean filter = service.toFilter(computerName);
        	if (filter){
        		return true;
        	}else{
        		return super.doProcess(inputRecord);
        	}
        }
    }
}

