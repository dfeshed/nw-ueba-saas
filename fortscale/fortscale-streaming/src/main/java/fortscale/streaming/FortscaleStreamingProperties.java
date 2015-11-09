package fortscale.streaming;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FortscaleStreamingProperties {

	@Value("${impala.table.fields.data.source}")
	private String dataSourceFieldName;

	public String getDataSourceFieldName() {
		return dataSourceFieldName;
	}
	
}
