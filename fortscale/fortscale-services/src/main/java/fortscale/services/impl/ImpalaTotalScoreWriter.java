package fortscale.services.impl;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;

import fortscale.domain.analyst.ScoreConfiguration;
import fortscale.domain.analyst.ScoreWeight;
import fortscale.domain.core.ClassifierScore;
import fortscale.domain.core.User;
import fortscale.services.fe.Classifier;
import fortscale.utils.hdfs.HDFSWriter;
import fortscale.utils.impala.ImpalaParser;
import fortscale.utils.logging.Logger;

public class ImpalaTotalScoreWriter extends ImpalaWriter{
	private static Logger logger = Logger.getLogger(ImpalaTotalScoreWriter.class);
	
	private static ScoreWeight TOTAL_SCORE_WEIGHT = new ScoreWeight(Classifier.total.getId(), 0.0);
	
	private List<String> tableFieldsNames;
	private String tableFieldsDelimiter;
	
	public ImpalaTotalScoreWriter(File file, ImpalaParser impalaParser, List<String> tableFieldsNames, String tableFieldsDelimiter){
		super(file, impalaParser);
		this.tableFieldsNames = tableFieldsNames;
		this.tableFieldsDelimiter = tableFieldsDelimiter;
	}
	
	public ImpalaTotalScoreWriter(ImpalaParser impalaParser, List<String> tableFieldsNames, String tableFieldsDelimiter) {
		super(impalaParser);
		this.tableFieldsNames = tableFieldsNames;
		this.tableFieldsDelimiter = tableFieldsDelimiter;
	}
	
	public ImpalaTotalScoreWriter(HDFSWriter writer, ImpalaParser impalaParser, List<String> tableFieldsNames, String tableFieldsDelimiter) {
		super(writer, impalaParser);
		this.tableFieldsNames = tableFieldsNames;
		this.tableFieldsDelimiter = tableFieldsDelimiter;
	}

	public void writeScores(User user, Date timestamp, ScoreConfiguration scoreConfiguration){
		writeScore(user, TOTAL_SCORE_WEIGHT, timestamp, getTotalScoreExplanation(user, scoreConfiguration));
		for(ScoreWeight scoreWeight: scoreConfiguration.getConfMap().values()){
			writeScore(user, scoreWeight, timestamp, " ");
		}
	}
	
	private String getTotalScoreExplanation(User user, ScoreConfiguration scoreConfiguration){
		StringBuilder builder = new StringBuilder();
		for(ScoreWeight scoreWeight: scoreConfiguration.getConfMap().values()){
			appendClassifierScoreWeightDescription(builder, user, scoreWeight);
		}
		return builder.toString();
	}
	
	private void appendClassifierScoreWeightDescription(StringBuilder builder, User user, ScoreWeight scoreWeight){
		ClassifierScore classifierScore = user.getScore(scoreWeight.getId());
		if(classifierScore == null){
			return;
		}
		builder.append(scoreWeight.getId()).append("(score = ").append(classifierScore.getScore()).append(", weight = ").append(scoreWeight.getWeight()).append(")").append("   ");
	}
	
	private void writeScore(User user, ScoreWeight scoreWeight, Date timestamp, String scoreExplanation){
		ClassifierScore classifierScore = user.getScore(scoreWeight.getId());
		if(classifierScore == null){
			return;
		}
		
		TotalScoreView totalScoreView = new TotalScoreView(impalaParser, user, scoreWeight, timestamp, scoreExplanation, classifierScore);
		List<String> values = new ArrayList<>();
		for(String fieldName: tableFieldsNames){
			try {
				values.add(BeanUtils.getProperty(totalScoreView, fieldName));
			} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
				logger.warn(String.format("got the following exception while trying to read the field %s", fieldName), e);
				values.add("NULL");
			}
		}
		writeLine(StringUtils.join(values, tableFieldsDelimiter), totalScoreView.getRuntime());		
	}
	
//	private void writeScore(User user, String classifierId){
//		ClassifierScore classifierScore = user.getScore(classifierId);
//		if(classifierScore == null){
//			write("| | | ");
//		} else{
//			String csvLineString = String.format("|%s|%s|%s",getRuntime(classifierScore.getTimestamp()), classifierScore.getScore(), classifierScore.getAvgScore());
//			write(csvLineString);
//		}
//	}
}
