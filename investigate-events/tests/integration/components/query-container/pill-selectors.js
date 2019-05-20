/**
 * Selectors for anything contained within a query filter pill.
 */
const metaTrigger = '.pill-meta .ember-power-select-trigger';
const operatorTrigger = '.pill-operator .ember-power-select-trigger';
const valueTrigger = '.pill-value .ember-power-select-trigger';

const powerSelectDropdown = '.ember-power-select-dropdown';
const newPillTriggerContainer = '.new-pill-trigger-container';

const triggerMetaPowerSelect = `${newPillTriggerContainer} ${metaTrigger}`;
const triggerOperatorPowerSelect = `${newPillTriggerContainer} ${operatorTrigger}`;
const triggerValuePowerSelect = `${newPillTriggerContainer} ${valueTrigger}`;

export default {
  activePills: '.query-pills .is-active',
  activeQueryPill: '.query-pill.is-active',
  allPills: '.query-pills',
  complexPill: '.complex-pill',
  complexPillActive: '.complex-pill.is-active',
  complexPillInput: '.complex-pill input',
  complexPillInputFocus: '.complex-pill input:focus',
  deletePill: '.delete-pill',
  expensiveIndicator: '.rsa-icon-stopwatch-lined.is-expensive',
  expensivePill: '.query-pill.is-expensive',
  focusedPill: '.is-focused',
  focusHolderInput: '.focus-holder input',
  freeFormInput: '.rsa-investigate-free-form-query-bar input',
  freeFormInputFocus: '.rsa-investigate-free-form-query-bar input:focus',
  invalidPill: '.is-invalid',
  loadingQueryButton: '.execute-query-button .rsa-loader',
  meta: '.pill-meta',
  metaInput: '.pill-meta input',
  metaInputFocused: '.pill-meta input:focus',
  metaSelectInput: '.pill-meta .ember-power-select-trigger input',
  metaTrigger,
  newPillTemplate: '.new-pill-template',
  newPillTrigger: '.new-pill-trigger',
  newPillTriggerContainer,
  operator: '.pill-operator',
  operatorSelectInput: '.pill-operator .ember-power-select-trigger input',
  operatorTrigger,
  pillOpen: '.pill-open',
  pillOpenForEdit: '.pill-open-for-edit',
  pillTriggerOpenForAdd: '.pill-trigger-open-for-add',
  populatedItem: '.is-populated',
  powerSelectAfterOption: '.ember-power-select-after-option',
  powerSelectAfterOptionHighlight: '.ember-power-select-after-option[aria-current="true"]',
  powerSelectAfterOptions: '.ember-power-select-after-options',
  powerSelectDropdown,
  powerSelectOption: '.js-test-power-select-option',
  queryPill: '.query-pill',
  selectedPill: '.is-selected',
  textPill: '.text-pill',
  textPillActive: '.text-pill.is-active',
  textPillInput: '.text-pill input',
  textPillInputFocus: '.text-pill input:focus',
  triggerMetaPowerSelect,
  triggerOperatorPowerSelect,
  triggerValuePowerSelect,
  value: '.pill-value',
  valueSelectInput: '.pill-value .ember-power-select-trigger input',
  valueTrigger,
  pillTabs: '.ember-power-select-after-options .power-select-tabs',
  metaTab: '.ember-power-select-after-options .power-select-tabs .meta-tab',
  metaTabSelected: '.ember-power-select-after-options .power-select-tabs .meta-tab.selected',
  recentQueriesTab: '.ember-power-select-after-options .power-select-tabs .recent-queries-tab',
  recentQueriesTabSelected: '.ember-power-select-after-options .power-select-tabs .recent-queries-tab.selected',
  powerSelectNoMatch: '.ember-power-select-option--no-matches-message',
  recentQueriesOptionsInMeta: '.recent-queries-option.meta',
  recentQueriesOptionsInOperator: '.recent-queries-option.operator',
  recentQueriesOptionsInValue: '.recent-queries-option.value',
  powerSelectOptionValue: '.js-test-power-select-option .value'
};