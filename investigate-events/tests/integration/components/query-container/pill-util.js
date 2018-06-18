import { fillIn, find, waitUntil, triggerKeyEvent } from '@ember/test-helpers';
import { selectChoose } from 'ember-power-select/test-support/helpers';

import * as MESSAGE_TYPES from 'investigate-events/components/query-container/message-types';
import PILL_SELECTORS from './pill-selectors';

const ENTER_KEY = 13;
const X_KEY = 88;

const pillSelectors = {
  metaPowerSelect: PILL_SELECTORS.metaTrigger,
  operatorPowerSelect: PILL_SELECTORS.operatorTrigger,
  powerSelectOption: PILL_SELECTORS.powerSelectOption,
  valueInput: PILL_SELECTORS.valueInput
};

const pillTriggerSelectors = {
  metaPowerSelect: PILL_SELECTORS.triggerMetaPowerSelect,
  operatorPowerSelect: PILL_SELECTORS.triggerOperatorPowerSelect,
  powerSelectOption: PILL_SELECTORS.powerSelectOption,
  valueInput: PILL_SELECTORS.triggerValueInput
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

export const isIgnoredInitialEvent = (event) => {
  return [MESSAGE_TYPES.PILL_ENTERED].includes(event);
};