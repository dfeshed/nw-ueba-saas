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
  queryPill: '.query-pill',
  allPills: '.query-pills',
  complexPill: '.complex-pill',
  complexPillInput: '.complex-pill input',
  complexPillInputFocus: '.complex-pill input:focus',
  complexPillActive: '.complex-pill.is-active',
  deletePill: '.delete-pill',
  freeFormInput: '.rsa-investigate-free-form-query-bar input',
  freeFormInputFocus: '.rsa-investigate-free-form-query-bar input:focus',
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
  powerSelectOption: '.js-test-power-select-option',
  powerSelectOptionHighlight: '.ember-power-select-after-option[aria-current="true"]',
  triggerMetaPowerSelect,
  triggerOperatorPowerSelect,
  triggerValuePowerSelect,
  value: '.pill-value',
  valueSelectInput: '.pill-value .ember-power-select-trigger input',
  valueTrigger,
  activePills: '.query-pills .is-active',
  invalidPill: '.is-invalid',
  selectedPill: '.is-selected',
  populatedItem: '.is-populated',
  expensivePill: '.query-pill.is-expensive',
  expensiveIndicator: '.rsa-icon-stopwatch-lined.is-expensive',
  focusHolderInput: '.focus-holder input',
  activeQueryPill: '.query-pill.is-active',
  focusedPill: '.is-focused',
  loadingQueryButton: '.execute-query-button .rsa-loader',
  powerSelectDropdown,
  powerSelectAfterOptions: '.ember-power-select-after-options',
  powerSelectAfterOption: '.ember-power-select-after-option'
};