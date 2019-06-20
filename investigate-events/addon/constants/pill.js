export const AFTER_OPTION_FREE_FORM_LABEL = 'Free-Form Filter';
export const AFTER_OPTION_TEXT_LABEL = 'Text Filter';
export const AFTER_OPTION_TEXT_DISABLED_LABEL = 'Text Filter is unavailable. Only one is permitted.';
export const AFTER_OPTION_TEXT_UNAVAILABLE_LABEL = 'Text Filter is unavailable. All services must be 11.3 or greater.';
export const AFTER_OPTION_QUERY_LABEL = 'Query Filter';
export const AFTER_OPTION_TAB_META = 'meta';
export const AFTER_OPTION_TAB_RECENT_QUERIES = 'recent queries';
// TODO: Removing "!" for now as it's causing issues with "!=" being flagged as
// complex.
export const COMPLEX_OPERATORS = ['<=', '<', '>=', '>', '&&', '||', '(', ')', ',', '-', 'AND', 'NOT', 'OR', 'length', 'regex'];
export const COMPLEX_FILTER = 'complex';
export const OPERATORS = ['!exists', 'exists', 'contains', 'begins', 'ends', '!=', '='];
export const QUERY_FILTER = 'query';
export const SEARCH_TERM_MARKER = '\u02F8'; // RAISED COLON "˸"
export const TEXT_FILTER = 'text';
export const PILL_META_DATA_SOURCE = 'pill-meta';
export const PILL_OPERATOR_DATA_SOURCE = 'pill-operator';
export const PILL_VALUE_DATA_SOURCE = 'pill-value';

// Selectors
export const NO_RESULTS_MESSAGE_SELECTOR = '.investigate-query-dropdown .ember-power-select-option.ember-power-select-option--no-matches-message';
export const LOADING_SPINNER_SELECTOR = '.investigate-query-dropdown .ember-power-select-options .ember-power-select-loading-options-spinner';
export const POWER_SELECT_INPUT = '.ember-power-select-typeahead-input';
export const POWER_SELECT_TRIGGER_INPUT = '.ember-power-select-trigger input';
export const POWER_SELECT_OPTIONS = '.investigate-query-dropdown .ember-power-select-options';