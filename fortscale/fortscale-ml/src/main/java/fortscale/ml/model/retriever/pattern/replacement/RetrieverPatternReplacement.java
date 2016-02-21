package fortscale.ml.model.retriever.pattern.replacement;

public class RetrieverPatternReplacement {
	String pattern;
	String replacement;
	String preReplacementCondition;
	String postReplacementCondition;

	public RetrieverPatternReplacement(RetrieverPatternReplacementConf retrieverPatternReplacementConf) {
		pattern = retrieverPatternReplacementConf.getPattern();
		replacement = retrieverPatternReplacementConf.getReplacement();
		preReplacementCondition = retrieverPatternReplacementConf.getPreReplacementCondition();
		postReplacementCondition = retrieverPatternReplacementConf.getPostReplacementCondition();
	}

	public String replacePattern(String original) {
		if (original == null) {
			return null;
		}

		if (preReplacementCondition != null && !original.matches(preReplacementCondition)) {
			return original;
		}

		String result = original.replaceAll(pattern, replacement);

		if (postReplacementCondition != null && !result.matches(postReplacementCondition)) {
			return original;
		}

		return result;
	}
}
