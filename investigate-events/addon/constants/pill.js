export const AFTER_OPTION_FREE_FORM_LABEL = 'Free-Form Filter';
export const AFTER_OPTION_TEXT_LABEL = 'Text Filter';
export const AFTER_OPTION_TEXT_DISABLED_LABEL = 'Text Filter is unavailable. Only one is permitted.';
export const AFTER_OPTION_TEXT_UNAVAILABLE_LABEL = 'Text Filter is unavailable. All services must be 11.3 or greater.';
export const AFTER_OPTION_TAB_META = 'meta';
export const AFTER_OPTION_TAB_RECENT_QUERIES = 'recent queries';
export const CLOSE_PAREN = 'close-paren';
// TODO: Removing "!" for now as it's causing issues with "!=" being flagged as
// complex.
export const LOGICAL_OPERATORS = ['&&', 'AND', '||', 'OR'];
// TODO - Move "NOT" up to LOGICAL_OPERATORS once we support that
export const COMPLEX_OPERATORS = [...LOGICAL_OPERATORS, '(', ')', 'NOT'];
export const COMPLEX_FILTER = 'complex';
export const OPEN_PAREN = 'open-paren';
export const OPERATOR_AND = 'operator-and';
export const OPERATOR_OR = 'operator-or';
export const OPERATORS = ['!exists', 'exists', 'contains', 'begins', 'ends', '!=', '='];
export const PILL_META_DATA_SOURCE = 'pill-meta';
export const PILL_OPERATOR_DATA_SOURCE = 'pill-operator';
export const PILL_VALUE_DATA_SOURCE = 'pill-value';
export const PILL_RECENT_QUERY_DATA_SOURCE = 'recent-query';
export const POWER_SELECT_OPTIONS_QUERY_LABEL = 'Query Filter';
export const QUERY_FILTER = 'query';
export const SEARCH_TERM_MARKER = '\u02F8'; // RAISED COLON "˸"
export const TEXT_FILTER = 'text';
export const DELETE_PILL = 'delete-pill';

// Selectors
export const LOADING_SPINNER_SELECTOR = '.investigate-query-dropdown .ember-power-select-options .ember-power-select-loading-options-spinner';
export const NO_RESULTS_MESSAGE_SELECTOR = '.investigate-query-dropdown .ember-power-select-option.ember-power-select-option--no-matches-message';
export const POWER_SELECT_INPUT = '.ember-power-select-typeahead-input';
export const POWER_SELECT_OPTIONS = '.investigate-query-dropdown .ember-power-select-options';
export const POWER_SELECT_TRIGGER_INPUT = '.ember-power-select-trigger input';