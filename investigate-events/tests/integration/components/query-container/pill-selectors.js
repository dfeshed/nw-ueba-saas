
const metaTrigger = '.pill-meta .ember-power-select-trigger';
const operatorTrigger = '.pill-operator .ember-power-select-trigger';
const valueInput = '.pill-value input';

const newPillTriggerContainer = '.new-pill-trigger-container';

const triggerMetaPowerSelect = `${newPillTriggerContainer} ${metaTrigger}`;
const triggerOperatorPowerSelect = `${newPillTriggerContainer} ${operatorTrigger}`;
const triggerValueInput = `${newPillTriggerContainer} ${valueInput}`;

export default {
  allPills: '.query-pills',
  deletePill: '.delete-pill',
  meta: '.pill-meta',
  metaInput: '.pill-meta input',
  metaSelectInput: '.pill-meta .ember-power-select-trigger input',
  metaTrigger,
  newPillTemplate: 'new-pill-template',
  newPillTrigger: '.new-pill-trigger',
  newPillTriggerContainer,
  operator: '.pill-operator',
  operatorInput: '.pill-operator .ember-power-select-trigger input',
  operatorTrigger,
  pillOpen: '.pill-open',
  pillOpenForEdit: '.pill-open-for-edit',
  pillTriggerOpenForAdd: '.pill-trigger-open-for-add',
  powerSelectOption: '.ember-power-select-option',
  triggerMetaPowerSelect,
  triggerOperatorPowerSelect,
  triggerValueInput,
  value: '.pill-value',
  valueInput,
  invalidPill: '.is-invalid',
  queryPill: '.query-pill'
};