package fortscale.utils;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Predicate;
import com.google.common.base.Strings;

/**
 * Utility class that holds string predicates to be used while filtering collections
 */
public final class StringPredicates {

	public static Predicate<String> endsWith(String suffix) {
		checkNotNull(suffix);
		return new StringEndsWithPredicate(suffix);
	}
	
	public static class StringEndsWithPredicate implements Predicate<String> {
		private String suffix;
		
		public StringEndsWithPredicate(String suffix) {
			this.suffix = suffix;
		}
		
		@Override
		public boolean apply(String input) {
			return !Strings.isNullOrEmpty(input) && input.endsWith(suffix);
		}
	}
	
	
}
