import { click, fillIn, findAll, focus, settled, triggerKeyEvent } from '@ember/test-helpers';
import { selectChoose } from 'ember-power-select/test-support/helpers';

import * as MESSAGE_TYPES from 'investigate-events/components/query-container/message-types';
import { AFTER_OPTION_TEXT_LABEL } from 'investigate-events/constants/pill';
import PILL_SELECTORS from './pill-selectors';
import KEY_MAP from 'investigate-events/util/keys';

const ENTER_KEY = 13;
const X_KEY = 88;
const ESCAPE_KEY = KEY_MAP.escape.code;

const ALL_META_OPTIONS = [
  // IndexedByValue
  { format: 'Float32', metaName: 'file.entropy', count: 4, flags: -2147482877, displayName: 'File Entropy', indexedBy: 'value', expensiveCount: 0 },
  // IndexedByValue
  { format: 'IPv4', metaName: 'alias.ip', count: 4, flags: -2147482621, displayName: 'IP Aliases', indexedBy: 'value', expensiveCount: 0 },
  // IndexByValue
  { format: 'IPv6', metaName: 'alias.ipv6', count: 4, flags: -2147482621, displayName: 'IPv6 Aliases', indexedBy: 'value', expensiveCount: 0 },
  // IndexedByValue
  { format: 'MAC', metaName: 'alias.mac', count: 4, flags: -2147482621, displayName: 'MAC Aliases', indexedBy: 'value', expensiveCount: 0 },
  // IndexedByValue and text
  { format: 'Text', metaName: 'alert', count: 7, flags: -2147483133, displayName: 'Alerts', indexedBy: 'value', expensiveCount: 2 },
  // IndexedByValue
  { format: 'TimeT', metaName: 'starttime', count: 4, flags: -2147482621, displayName: 'Time Start', indexedBy: 'value', expensiveCount: 0 },
  // IndexedByValue
  { format: 'UInt8', metaName: 'ip.proto', count: 4, flags: -2147482541, displayName: 'IP Protocol', indexedBy: 'value', expensiveCount: 0 },
  // IndexedByValue
  { format: 'UInt16', metaName: 'eth.type', count: 4, flags: -2147482541, displayName: 'Ethernet Protocol', indexedBy: 'value', expensiveCount: 0 },
  // IndexedByKey
  { format: 'UInt32', metaName: 'bytes.src', count: 4, flags: -2147482878, displayName: 'Bytes Sent', indexedBy: 'key', expensiveCount: 2 },
  // IndexedByKey
  { format: 'UInt64', metaName: 'filename.size', count: 4, flags: -2147482878, displayName: 'File Size', indexedBy: 'key', expensiveCount: 2 },
  // IndexedByKey and Text
  { format: 'Text', metaName: 'referer', count: 7, flags: -2147482878, displayName: 'Referer', indexedBy: 'key', expensiveCount: 5 },
  // special case - exists, !exists, =, !=
  { format: 'UInt64', metaName: 'sessionid', count: 4, flags: -2147483631, displayName: 'Session ID', indexedBy: 'none', expensiveCount: 0 }
];

const _metaNameForFormat = (format) => {
  const meta = ALL_META_OPTIONS.findBy('format', format);
  if (!meta) {
    throw new Error(`metaNameForFormat called with bad format: ${format}`);
  }
  return meta.metaName;
};

const pillSelectors = {
  metaPowerSelect: PILL_SELECTORS.metaTrigger,
  operatorPowerSelect: PILL_SELECTORS.operatorTrigger,
  valuePowerSelect: PILL_SELECTORS.valueTrigger,
  powerSelectOption: PILL_SELECTORS.powerSelectOption
};

const pillTriggerSelectors = {
  metaPowerSelect: PILL_SELECTORS.triggerMetaPowerSelect,
  operatorPowerSelect: PILL_SELECTORS.triggerOperatorPowerSelect,
  valuePowerSelect: PILL_SELECTORS.triggerValuePowerSelect,
  powerSelectOption: PILL_SELECTORS.powerSelectOption
};

export const createBasicPill = async function(fromTrigger, format, operator) {
  const selectors = fromTrigger ? pillTriggerSelectors : pillSelectors;

  if (format) {
    // choose the meta of format requested
    const meta = _metaNameForFormat(format);
    await selectChoose(selectors.metaPowerSelect, meta);
  } else {
    // Choose the first meta option
    await selectChoose(selectors.metaPowerSelect, selectors.powerSelectOption, 0); // option A
  }

  if (operator) {
    await selectChoose(selectors.operatorPowerSelect, operator);
  } else {
    // Choose the first operator option
    await selectChoose(selectors.operatorPowerSelect, '='); // option =
  }

  // Fill in the value, to properly simulate the event we need to fillIn AND
  // triggerKeyEvent for the "x" character.
  // await selectChoose(selectors.valuePowerSelect, 'x');
  await fillIn(PILL_SELECTORS.valueSelectInput, 'x');
  await triggerKeyEvent(PILL_SELECTORS.valueSelectInput, 'keydown', X_KEY); // x

  // Create pill
  await triggerKeyEvent(PILL_SELECTORS.valueSelectInput, 'keydown', ENTER_KEY);
};

const ignoredInitialMessageTypes = [
  MESSAGE_TYPES.PILL_ENTERED_FOR_APPEND_NEW,
  MESSAGE_TYPES.PILL_ENTERED_FOR_INSERT_NEW
];

export const isIgnoredInitialEvent = (event) => {
  return ignoredInitialMessageTypes.includes(event);
};

/*
 * Latest ember-test-helpers has a doubleClick, remove this
 * when that is introduced.
 *
 * Note: this is not an async function
 */
export const doubleClick = async(selector, skipExtraEvents) => {
  const element = document.querySelector(selector);
  const opts = { view: window, bubbles: true, cancelable: true };

  if (!skipExtraEvents) {
    element.dispatchEvent(new MouseEvent('mousedown', opts));
    element.dispatchEvent(new MouseEvent('focus', opts));
    element.dispatchEvent(new MouseEvent('mouseup', opts));
    element.dispatchEvent(new MouseEvent('click', opts));
    element.dispatchEvent(new MouseEvent('mousedown', opts));
    element.dispatchEvent(new MouseEvent('mouseup', opts));
    element.dispatchEvent(new MouseEvent('click', opts));
  }

  element.dispatchEvent(new MouseEvent('dblclick', opts));
  return settled();
};

export const elementIsVisible = (el) => {
  const style = window.getComputedStyle(el);
  return style.display !== 'none';
};

export const leaveNewPillTemplate = async() => {
  await click(PILL_SELECTORS.newPillTrigger);
  await focus(PILL_SELECTORS.triggerMetaPowerSelect);
  await triggerKeyEvent(PILL_SELECTORS.metaTrigger, 'keydown', ESCAPE_KEY);
};

export const clickTextFilterOption = async() => {
  const afterOptions = findAll(PILL_SELECTORS.powerSelectAfterOption);
  const textFilter = afterOptions.find((el) => el.textContent.includes(AFTER_OPTION_TEXT_LABEL));
  if (textFilter) {
    await click(textFilter);
  }
};