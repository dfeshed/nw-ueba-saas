package fortscale.ml.model.retriever.function;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import fortscale.common.util.GenericHistogram;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.Map;
import java.util.regex.Pattern;

public class DiscreteDataHistogramIgnorePattern implements IDataRetrieverFunction {
	public static final String DATA_RETRIEVER_FUNCTION_TYPE = "discrete_data_histogram_ignore_pattern";

	private Pattern ignorePattern;

	@JsonCreator
	public DiscreteDataHistogramIgnorePattern(@JsonProperty("ignorePattern") String ignorePattern) {
		if (ignorePattern != null) {
			this.ignorePattern = Pattern.compile(ignorePattern);
		}
	}

	@Override
	public Object execute(Object data, Date dataTime, Date currentTime) {
		GenericHistogram oldHistogram = (GenericHistogram)data;
		GenericHistogram newHistogram = new GenericHistogram();

		for (Map.Entry<String, Double> entry : oldHistogram.getHistogramMap().entrySet()) {
			if (!ignoreValue(entry.getKey())) {
				newHistogram.add(entry.getKey(), entry.getValue());
			}
		}

		return newHistogram;
	}

	private boolean ignoreValue(String value) {
		return StringUtils.isBlank(value) || (ignorePattern != null && ignorePattern.matcher(value).matches());
	}
}
