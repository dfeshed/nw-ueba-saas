/**
 * Selectors for items contained within the query container, excluding selectors
 * related to query filter pills.
 */
export default {
  // Top Level Query Bar Toggles
  queryFormatToggleLinks: '.query-bar-select-actions a',
  queryFormatFreeFormToggle: '.query-bar-select-actions .freeForm-link',
  queryFormatGuidedToggle: '.query-bar-select-actions .guided-link',
  queryButton: '.execute-query-button button',
  queryButtonDisabled: '.execute-query-button.is-disabled',
  queryButtonQueued: '[test-id=queryButtonQueued]',
  queryButtonExecuting: '[test-id=queryButtonExecuting]',
  queryButtonInactive: '[test-id=queryButtonInactive]',

  // Free Form Selectors
  freeFormQueryBar: '.query-bar-selection.freeForm',
  freeFormBarContainer: '.rsa-investigate-free-form-query-bar',
  freeFormQueryBarFocusedInput: '.rsa-investigate-free-form-query-bar input:focus',
  freeFormQueryBarInput: '.rsa-investigate-free-form-query-bar input',

  // Guided Selectors
  guidedQueryBar: '.query-bar-selection.guided',
  guidedQueryBarFocusedInput: '.new-pill-template .pill-meta.is-expanded'
};

