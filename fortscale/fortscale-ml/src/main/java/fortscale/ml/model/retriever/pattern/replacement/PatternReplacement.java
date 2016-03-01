package fortscale.ml.model.retriever.pattern.replacement;

public class PatternReplacement {
	private PatternReplacementConf patternReplacementConf;

	public PatternReplacement(PatternReplacementConf patternReplacementConf) {
		this.patternReplacementConf = patternReplacementConf;
	}

	public String replacePattern(String original) {
		return replacePattern(
				original,
				patternReplacementConf.getPattern(),
				patternReplacementConf.getReplacement(),
				patternReplacementConf.getPreReplacementCondition(),
				patternReplacementConf.getPostReplacementCondition());
	}

	public static String replacePattern(
			String original,
			String pattern,
			String replacement,
			String preReplacementCondition,
			String postReplacementCondition) {

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
