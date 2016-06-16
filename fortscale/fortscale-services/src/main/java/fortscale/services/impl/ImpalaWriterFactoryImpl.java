package fortscale.services.impl;


import java.io.File;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;

import fortscale.utils.impala.ImpalaParser;


public class ImpalaWriterFactoryImpl extends ImpalaWriterFactory{

	@Value("${impala.total.scores.table.fields}")
	private String impalaTotalScoringTableFields;
	@Value("${impala.total.scores.table.delimiter}")
	private String impalaTotalScoringTableDelimiter;
	
	private static File getFile(String path) {
		String fileSeperator = File.separator;
		if(fileSeperator == null || fileSeperator.equals("\\")) {
			path = path.replace("/", "\\");
		}
		File file = new File(path);
		return file;
	}

	public ImpalaUseridToAppUsernameWriter createImpalaUseridToAppUsernameWriter(){
		if(StringUtils.isEmpty(useridToAppUsernameCsvFileFullPathString)){
			return new ImpalaUseridToAppUsernameWriter(impalaParser);
		}
		ImpalaUseridToAppUsernameWriter writer = new ImpalaUseridToAppUsernameWriter(getFile(useridToAppUsernameCsvFileFullPathString), impalaParser);
		return writer;
	}
}
