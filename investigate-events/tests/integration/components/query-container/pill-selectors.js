/**
 * Selectors for anything contained within a query filter pill.
 */
const metaTrigger = '.pill-meta .ember-power-select-trigger';
const operatorTrigger = '.pill-operator .ember-power-select-trigger';
const valueTrigger = '.pill-value .ember-power-select-trigger';
const recentQueryTrigger = '.recent-query .ember-power-select-trigger';

const powerSelectDropdown = '.ember-power-select-dropdown';
const newPillTriggerContainer = '.new-pill-trigger-container';

const triggerMetaPowerSelect = `${newPillTriggerContainer} ${metaTrigger}`;
const triggerOperatorPowerSelect = `${newPillTriggerContainer} ${operatorTrigger}`;
const triggerValuePowerSelect = `${newPillTriggerContainer} ${valueTrigger}`;

export default {
  activePills: '.query-pills .is-active',
  activeQueryPill: '.query-pill.is-active',
  allPills: '.query-pills',
  closeParen: '.close-paren',
  closeParenFocused: '.close-paren.is-focused',
  closeParenTwinFocused: '.close-paren.is-twin-focused',
  closeParenSelected: '.close-paren.is-selected',
  complexPill: '.complex-pill',
  complexPillActive: '.complex-pill.is-active',
  complexPillInput: '.complex-pill input',
  complexPillInputFocus: '.complex-pill input:focus',
  deletePill: '.delete-pill',
  expensiveIndicator: '.rsa-icon-stopwatch.is-expensive',
  expensivePill: '.query-pill.is-expensive',
  focusedPill: '.is-focused',
  focusedQueryPill: '.query-pill.is-focused',
  focusedLogicalOperator: '.logical-operator.is-focused',
  focusHolderInput: '.focus-holder input',
  freeFormInput: '.rsa-investigate-free-form-query-bar input',
  freeFormInputFocus: '.rsa-investigate-free-form-query-bar input:focus',
  invalidPill: '.is-invalid',
  loadingQueryButton: '.execute-query-button .rsa-loader',
  logicalOperator: '.logical-operator',
  logicalOperatorAND: '.logical-operator.AND',
  logicalOperatorOR: '.logical-operator.OR',
  meta: '.pill-meta',
  metaInput: '.pill-meta input',
  metaInputFocused: '.pill-meta input:focus',
  metaSelectInput: '.pill-meta .ember-power-select-trigger input',
  metaTrigger,
  newPillTemplate: '.new-pill-template',
  newPillTemplateActive: '.new-pill-template.is-active',
  newPillTemplateRecentQuery: '.new-pill-template .recent-query .ember-power-select-trigger input',
  newPillTrigger: '.new-pill-trigger',
  newPillTriggerContainer,
  openParen: '.open-paren',
  openParenFocused: '.open-paren.is-focused',
  openParenTwinFocused: '.open-paren.is-twin-focused',
  openParenSelected: '.open-paren.is-selected',
  operator: '.pill-operator',
  operatorSelectInput: '.pill-operator .ember-power-select-trigger input',
  operatorTrigger,
  pillOpen: '.pill-open',
  pillOpenForEdit: '.pill-open-for-edit',
  pillTriggerOpenForAdd: '.pill-trigger-open-for-add',
  populatedItem: '.is-populated',
  powerSelectAfterOption: '.ember-power-select-after-option',
  powerSelectOptionHighlight: '.ember-power-select-option[aria-current="true"]',
  powerSelectAfterOptionHighlight: '.ember-power-select-after-option[aria-current="true"]',
  powerSelectAfterOptionDisabled: '.ember-power-select-after-option[aria-disabled="true"]',
  powerSelectAfterOptions: '.ember-power-select-after-options',
  powerSelectDropdown,
  powerSelectOption: '.js-test-power-select-option',
  powerSelectOptionValue: '.js-test-power-select-option .value',
  queryPill: '.query-pill',
  queryPillNotTemplate: '.query-pill:not(.new-pill-template)',
  quoteHighlight: '.quote-highlight',
  selectedPill: '.is-selected',
  textPill: '.text-pill',
  textPillActive: '.text-pill.is-active',
  textPillInput: '.text-pill input',
  textPillInputFocus: '.text-pill input:focus',
  textPillIcon: '.is-text-pill.rsa-icon',
  triggerMetaPowerSelect,
  triggerOperatorPowerSelect,
  triggerValuePowerSelect,
  value: '.pill-value',
  valueSelectInput: '.pill-value .ember-power-select-trigger input',
  valueTrigger,

  // PILL TABS
  pillTabs: '.ember-power-select-after-options .power-select-tabs',
  metaCount: '.meta-count',
  metaTab: '.ember-power-select-after-options .power-select-tabs .meta-tab',
  metaTabSelected: '.ember-power-select-after-options .power-select-tabs .meta-tab.selected',
  recentQueriesTab: '.ember-power-select-after-options .power-select-tabs .recent-queries-tab',
  recentQueriesTabSelected: '.ember-power-select-after-options .power-select-tabs .recent-queries-tab.selected',
  powerSelectNoMatch: '.ember-power-select-option--no-matches-message',

  // RECENT QUERIES
  recentQuery: '.recent-query',
  recentQuerySelectInput: '.recent-query .ember-power-select-trigger input',
  recentQueryTrigger,
  recentQueriesOptions: '.recent-queries-option.value',
  loadingSpinnerSelector: '.investigate-query-dropdown .ember-power-select-options .ember-power-select-loading-options-spinner',
  noResultsMessageSelector: '.investigate-query-dropdown .ember-power-select-option.ember-power-select-option--no-matches-message',
  recentQueryCount: '.recent-query-count',

  powerSelectGroup: '.ember-power-select-group',
  powerSelectGroupName: '.ember-power-select-group-name'
};
