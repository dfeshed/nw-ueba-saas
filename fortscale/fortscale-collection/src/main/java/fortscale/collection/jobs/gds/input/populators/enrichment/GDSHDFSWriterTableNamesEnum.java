package fortscale.collection.jobs.gds.input.populators.enrichment;

/**
 * enum for different formats of the steps - used to write to different sources
 * Created by galiar on 21/01/2016.
 */
public enum GDSHDFSWriterTableNamesEnum {

	ENRICH("enriched","enricheddata","enriched",""),
	SCORE("scored","score","score",""),
	TOP_SCORE("top_scored","score","score_top","top");

	private String taskName;
	private String hdfsTableName;
	private String levelDBTableName;
	//create in order to generate the following pattern: ...score.<datasorce>.top...
	private String suffixHdfsTableName;

	GDSHDFSWriterTableNamesEnum(String taskName, String hdfsTableName, String levelDBTableName,String suffixHdfsTableName) {
		this.taskName = taskName;
		this.hdfsTableName = hdfsTableName;
		this.levelDBTableName = levelDBTableName;
		this.suffixHdfsTableName = suffixHdfsTableName;
	}


	public String getTaskName() {
		return taskName;
	}

	public String getHdfsTableName() {
		return hdfsTableName;
	}

	public String getLevelDBTableName() {
		return levelDBTableName;
	}
	public String getSuffixHdfsTableName() {
		return suffixHdfsTableName;
	}


}
