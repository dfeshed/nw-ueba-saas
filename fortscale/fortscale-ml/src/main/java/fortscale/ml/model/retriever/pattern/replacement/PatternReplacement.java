package fortscale.ml.model.retriever.pattern.replacement;

public class PatternReplacement {
	String pattern;
	String replacement;
	String preReplacementCondition;
	String postReplacementCondition;

	public PatternReplacement(PatternReplacementConf patternReplacementConf) {
		pattern = patternReplacementConf.getPattern();
		replacement = patternReplacementConf.getReplacement();
		preReplacementCondition = patternReplacementConf.getPreReplacementCondition();
		postReplacementCondition = patternReplacementConf.getPostReplacementCondition();
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
