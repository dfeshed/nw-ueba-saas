
package fortscale.collection.morphlines.commands;

import java.util.Collection;
import java.util.Collections;

import org.kitesdk.morphline.api.Command;
import org.kitesdk.morphline.api.CommandBuilder;
import org.kitesdk.morphline.api.MorphlineContext;
import org.kitesdk.morphline.api.Record;
import org.kitesdk.morphline.base.AbstractCommand;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;

import com.typesafe.config.Config;

import fortscale.collection.morphlines.RecordExtensions;
import fortscale.services.impl.UsernameNormalizer;

@Deprecated
@Configurable()
public class NormalizeUsernameMorphCmdBuilder implements CommandBuilder {
	
	protected String usernameField;
	protected String domainField;
    @Value("${normalizedUser.fail.filter:false}")
    private boolean dropOnFail;

	
	@Override
	public Collection<String> getNames() {
		return Collections.singletonList("NormalizeUsername");
	}

	@Override
	public Command build(Config config, Command parent, Command child, MorphlineContext context) {
		return new NormalizeUsername(this, config, parent, child, context);
	}
	
	protected UsernameNormalizer getUsernameNormalizer(){
		return null;
	}
	
	protected String normalizeUsername(Record inputRecord){
        //if normalizedUsers.fail filter is set: function returns null if username normalization failed.
		String ret = null;
		UsernameNormalizer usernameNormalizer = getUsernameNormalizer();
		if(usernameNormalizer != null){
            ret = usernameNormalizer.normalize(RecordExtensions.getStringValue(inputRecord, usernameField)
					.toLowerCase(), RecordExtensions.getStringValue(inputRecord, domainField), null, false);
        }  
        
		return ret;
	}

	protected boolean toDropRecord(String normalizedUsername, Record inputRecord){
		 if (normalizedUsername == null && dropOnFail == true){
             return true;
         }
		 return false;
	}
	
	protected String getFinalNormalizedUserName(Record inputRecord, String normalizedUserName){
		if(normalizedUserName != null){
			return normalizedUserName;
		}
		
		String username = RecordExtensions.getStringValue(inputRecord, usernameField).toLowerCase();
        return username;
	}
	
	// /////////////////////////////////////////////////////////////////////////////
	// Nested classes:
	// /////////////////////////////////////////////////////////////////////////////
	private class NormalizeUsername extends AbstractCommand {
		
		private String normalizedUsernameField;
		
		public NormalizeUsername(CommandBuilder builder, Config config, Command parent, Command child, MorphlineContext context) {
			super(builder, config, parent, child, context);
			usernameField = getConfigs().getString(config, "usernameField");
			this.normalizedUsernameField = getConfigs().getString(config, "normalizedUsernameField");
			
			validateArguments();
		}

		@Override
		protected boolean doProcess(Record inputRecord) {
			// If we weren't able to connect or access the collection,
			// return an empty string
            String normalizedUserName = normalizeUsername(inputRecord);
            if(toDropRecord(normalizedUserName, inputRecord)){
            	return true;
            }
            inputRecord.put(normalizedUsernameField, getFinalNormalizedUserName(inputRecord, normalizedUserName));
			return super.doProcess(inputRecord);

		}
	}
}

