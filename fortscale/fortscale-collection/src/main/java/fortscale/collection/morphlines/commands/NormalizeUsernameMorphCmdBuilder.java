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

import com.google.common.base.Objects;
import com.typesafe.config.Config;

import fortscale.collection.morphlines.RecordExtensions;
import fortscale.services.impl.UsernameNormalizer;

@Configurable()
public class NormalizeUsernameMorphCmdBuilder implements CommandBuilder {
	
	protected String usernameField;
    @Value("${normalizedUser.fail.filter:}")
    private String dropOnFail;

	
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
	
	protected String normalizeUsername(Record record){
        //if normalizedUsers.fail filter is set: function returns null if username normalization failed.
		String ret = RecordExtensions.getStringValue(record, usernameField).toLowerCase();
		UsernameNormalizer usernameNormalizer = getUsernameNormalizer();
		if(usernameNormalizer != null){
            String normalizedName = usernameNormalizer.normalize(ret);
            if(dropOnFail != null && dropOnFail.equals("true")){
                ret = normalizedName;
            }else{
                ret = Objects.firstNonNull(normalizedName, ret);
            }
        }  
        
		return ret;
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
            if (normalizedUserName == null && dropOnFail != null && dropOnFail.equals("true")){
                return true;
            }
            inputRecord.put(normalizedUsernameField, normalizedUserName);

			return super.doProcess(inputRecord);

		}
	}
}
