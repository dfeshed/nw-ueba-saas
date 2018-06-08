import { fillIn, find, waitUntil, triggerKeyEvent } from '@ember/test-helpers';
import { selectChoose } from 'ember-power-select/test-support/helpers';

const ENTER_KEY = 13;
const X_KEY = 88;

const pillSelectors = {
  metaPowerSelect: '.pill-meta .ember-power-select-trigger',
  operatorPowerSelect: '.pill-operator .ember-power-select-trigger',
  powerSelectOption: '.ember-power-select-option',
  valueInput: '.pill-value input'
};

const pillTriggerSelectors = {
  metaPowerSelect: '.new-pill-trigger-container .pill-meta .ember-power-select-trigger',
  operatorPowerSelect: '.new-pill-trigger-container .pill-operator .ember-power-select-trigger',
  powerSelectOption: '.ember-power-select-option',
  valueInput: '.new-pill-trigger-container .pill-value input'
};

export const createBasicPill = async function(fromTrigger) {
  const selectors = fromTrigger ? pillTriggerSelectors : pillSelectors;
  // Choose the first meta option
  selectChoose(selectors.metaPowerSelect, selectors.powerSelectOption, 0); // option A
  await waitUntil(() => find(selectors.operatorPowerSelect));

  // Choose the first operator option
  selectChoose(selectors.operatorPowerSelect, selectors.powerSelectOption, 0); // option =
  await waitUntil(() => find(selectors.valueInput));

  // Fill in the value, to properly simulate the event we need to fillIn AND
  // triggerKeyEvent for the "x" character.
  await fillIn(selectors.valueInput, 'x');
  await triggerKeyEvent(selectors.valueInput, 'keydown', X_KEY); // x
  await triggerKeyEvent(selectors.valueInput, 'keydown', ENTER_KEY);
};