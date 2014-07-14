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
import org.springframework.beans.factory.annotation.Value;


import com.typesafe.config.Config;

import fortscale.domain.core.Computer;
import fortscale.domain.core.dao.ComputerRepository;
import fortscale.utils.actdir.ADParser;

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
        private ComputerRepository computerRepository;
        @Value("${machines.ou.filter:}")
        private String ouName; 
        private String hostnameField;
        private String regex;
        private Pattern regexMatcher;
        private String regexReplacement;
        
        
        HashMap<String, Boolean> OUmachines = null;
        public FilterOUMachine(CommandBuilder builder, Config config,
                Command parent, Command child, MorphlineContext context) {
            super(builder, config, parent, child, context);
            
            OUmachines = new HashMap<String, Boolean>();
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
        	if(ouName == null || ouName.equals("")){
        		return super.doProcess(inputRecord);
        	}
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
        	
            if(OUmachines.containsKey(computerName)){ //drop record
                if(OUmachines.get(computerName).equals(false)){
                    return true;
                }
            }else{
                Computer computer = computerRepository.findByName(computerName);
                if(computer == null){
                    OUmachines.put(computerName, false);
                    return true;
                }else{
                    String dn = computer.getDistinguishedName();
                    ADParser parser = new ADParser();
                    String computerOU = parser.parseOUFromDN(dn);
                    if(computerOU != null && computerOU.split("=")[1].equals(ouName)){
                        OUmachines.put(computerName, true);
                    }else{
                        OUmachines.put(computerName, false);
                        return true;
                    }
                }
            }
            return super.doProcess(inputRecord);
        }

    }
}

