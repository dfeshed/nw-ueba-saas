package fortscale.collection.morphlines.commands;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.hive.jdbc.HiveDriver;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import org.kitesdk.morphline.api.Command;
import org.kitesdk.morphline.api.CommandBuilder;
import org.kitesdk.morphline.api.MorphlineContext;
import org.kitesdk.morphline.api.Record;
import org.kitesdk.morphline.base.AbstractCommand;
import com.typesafe.config.Config;

public class GetHostnameBuilder implements CommandBuilder {

	@Override
	public Collection<String> getNames() {
		return Collections.singletonList("GetHostname");
	}

	@Override
	public Command build(Config config, Command parent, Command child,
			MorphlineContext context) {
		return new GetHostname(this, config, parent, child, context);
	}

	///////////////////////////////////////////////////////////////////////////////
	// Nested classes:
	///////////////////////////////////////////////////////////////////////////////
	private static final class GetHostname extends AbstractCommand {

		private final String ipAddress;
		private final String outputRecordName;
		private final String impalaServer;
		private final String impalaPort;

		public GetHostname(CommandBuilder builder, Config config, Command parent, Command child,
				MorphlineContext context) {
			super(builder, config, parent, child, context);
			this.ipAddress = getConfigs().getString(config, "ipAddress");
			this.impalaServer = getConfigs().getString(config, "impalaServer");
			this.impalaPort = getConfigs().getString(config, "impalaPort");
			this.outputRecordName = getConfigs().getString(config, "outputRecordName");
			validateArguments();
		}

		@Override
		protected boolean doProcess(Record inputRecord)  {
			List<?> tmp = inputRecord.get(this.ipAddress );
			String ip = null;
			if (tmp != null && tmp.size() > 0)
			{
				ip =(String) tmp.get(0);
			}
			String impalaSever = this.impalaServer;
			if (ip!=null)
			{
				inputRecord.put(this.outputRecordName, getHostname(ip,impalaSever,impalaPort));
			}
			return super.doProcess(inputRecord);

		}
		private String getHostname(String ip,String impalaSever,String impalaPort)
		{
			String Hostname = "";
			JdbcTemplate impalaJdbcTemplate = new JdbcTemplate(new SimpleDriverDataSource(new HiveDriver(), String.format("jdbc:hive2://%s:%s/;auth=noSasl", impalaSever,impalaPort)));
			List<Map<String, Object>> resultsMapTable = null;
			resultsMapTable = impalaJdbcTemplate.query(String.format("select machinename,ip,eventtimeepoch from lastcomputer where ip=\"%s\"",ip), new ColumnMapRowMapper());
			/*try
			{
				resultsMapTable = impalaJdbcTemplate2.query(String.format("select machinename,ip,eventtimeepoch from lastcomputer where ip=\"%s\"",ip), new ColumnMapRowMapper());
			}
			catch(Exception ex)
			{
			}*/
			long  maxDate =0;
			String computerName = null;
			if (resultsMapTable != null)
			{
				for (Map<String, Object> computerNameObj : resultsMapTable) {
					if (computerNameObj.get("eventtimeepoch") !=null)
					{
						if (maxDate < Long.parseLong(computerNameObj.get("eventtimeepoch").toString()))
						{
							maxDate = Long.parseLong(computerNameObj.get("eventtimeepoch").toString());
							computerName = computerNameObj.get("machinename").toString();
						}
					}
				}       

				if (System.currentTimeMillis()/1000-maxDate < 86400)
				{
					Hostname = computerName;
				}			
			}
			return Hostname;
		}
	}
}

