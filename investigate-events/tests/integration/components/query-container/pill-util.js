import { fillIn, find, waitUntil, triggerKeyEvent } from '@ember/test-helpers';
import { selectChoose } from 'ember-power-select/test-support/helpers';

const ENTER_KEY = 13;
const X_KEY = 88;

const metaPowerSelect = '.pill-meta .ember-power-select-trigger';
const operatorPowerSelect = '.pill-operator .ember-power-select-trigger';
const powerSelectOption = '.ember-power-select-option';
const valueInput = '.pill-value input';

export const createBasicPill = async function() {
  // Choose the first meta option
  selectChoose(metaPowerSelect, powerSelectOption, 0); // option A
  await waitUntil(() => find(operatorPowerSelect));

  // Choose the first operator option
  selectChoose(operatorPowerSelect, powerSelectOption, 0); // option =
  await waitUntil(() => find(valueInput));

  // Fill in the value, to properly simulate the event we need to fillIn AND
  // triggerKeyEvent for the "x" character.
  await fillIn(valueInput, 'x');
  await triggerKeyEvent(valueInput, 'keydown', X_KEY); // x
  await triggerKeyEvent(valueInput, 'keydown', ENTER_KEY);
};