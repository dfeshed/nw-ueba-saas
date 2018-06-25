import { fillIn, find, waitUntil, triggerKeyEvent } from '@ember/test-helpers';
import { selectChoose } from 'ember-power-select/test-support/helpers';

import * as MESSAGE_TYPES from 'investigate-events/components/query-container/message-types';
import PILL_SELECTORS from './pill-selectors';

const ENTER_KEY = 13;
const X_KEY = 88;

const ALL_META_OPTIONS = [
  { format: 'Float32', metaName: 'file.entropy', count: 4, flags: -2147482877, displayName: 'File Entropy', indexedBy: 'value', expensiveCount: 0 }, // IndexedByValue
  { format: 'IPv4', metaName: 'alias.ip', count: 4, flags: -2147482621, displayName: 'IP Aliases', indexedBy: 'value', expensiveCount: 0 },          // IndexedByValue
  { format: 'IPv6', metaName: 'alias.ipv6', count: 4, flags: -2147482621, displayName: 'IPv6 Aliases', indexedBy: 'value', expensiveCount: 0 },      // IndexByValue
  { format: 'MAC', metaName: 'alias.mac', count: 4, flags: -2147482621, displayName: 'MAC Aliases', indexedBy: 'value', expensiveCount: 0 },         // IndexedByValue
  { format: 'Text', metaName: 'alert', count: 7, flags: -2147483133, displayName: 'Alerts', indexedBy: 'value', expensiveCount: 2 },                 // IndexedByValue and text
  { format: 'TimeT', metaName: 'starttime', count: 4, flags: -2147482621, displayName: 'Time Start', indexedBy: 'value', expensiveCount: 0 },        // IndexedByValue
  { format: 'UInt8', metaName: 'ip.proto', count: 4, flags: -2147482541, displayName: 'IP Protocol', indexedBy: 'value', expensiveCount: 0 },        // IndexedByValue
  { format: 'UInt16', metaName: 'eth.type', count: 4, flags: -2147482541, displayName: 'Ethernet Protocol', indexedBy: 'value', expensiveCount: 0 }, // IndexedByValue
  { format: 'UInt32', metaName: 'bytes.src', count: 4, flags: -2147482878, displayName: 'Bytes Sent', indexedBy: 'key', expensiveCount: 2 },       // IndexedByKey
  { format: 'UInt64', metaName: 'filename.size', count: 4, flags: -2147482878, displayName: 'File Size', indexedBy: 'key', expensiveCount: 2 },    // IndexedByKey
  { format: 'Text', metaName: 'referer', count: 7, flags: -2147482878, displayName: 'Referer', indexedBy: 'key', expensiveCount: 5 },              // IndexedByKey and Text
  { format: 'UInt64', metaName: 'sessionid', count: 4, flags: -2147483631, displayName: 'Session ID', indexedBy: 'none', expensiveCount: 0 }        // special case - exists, !exists, =, !=
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
  powerSelectOption: PILL_SELECTORS.powerSelectOption,
  valueInput: PILL_SELECTORS.valueInput
};

const pillTriggerSelectors = {
  metaPowerSelect: PILL_SELECTORS.triggerMetaPowerSelect,
  operatorPowerSelect: PILL_SELECTORS.triggerOperatorPowerSelect,
  powerSelectOption: PILL_SELECTORS.powerSelectOption,
  valueInput: PILL_SELECTORS.triggerValueInput
};

export const createBasicPill = async function(fromTrigger, format) {
  const selectors = fromTrigger ? pillTriggerSelectors : pillSelectors;

  if (format) {
    // choose the meta of format requested
    const meta = _metaNameForFormat(format);
    selectChoose(selectors.metaPowerSelect, meta);
  } else {
    // Choose the first meta option
    selectChoose(selectors.metaPowerSelect, selectors.powerSelectOption, 0); // option A
  }
  await waitUntil(() => find(selectors.operatorPowerSelect));

  // Choose the first operator option
  selectChoose(selectors.operatorPowerSelect, '='); // option =
  await waitUntil(() => find(selectors.valueInput));

  // Fill in the value, to properly simulate the event we need to fillIn AND
  // triggerKeyEvent for the "x" character.
  await fillIn(selectors.valueInput, 'x');
  await triggerKeyEvent(selectors.valueInput, 'keydown', X_KEY); // x
  await triggerKeyEvent(selectors.valueInput, 'keydown', ENTER_KEY);
};

const ignoredInitialMessageTypes = [
  MESSAGE_TYPES.PILL_ENTERED_FOR_APPEND_NEW,
  MESSAGE_TYPES.PILL_ENTERED_FOR_EDIT,
  MESSAGE_TYPES.PILL_ENTERED_FOR_INSERT_NEW
];

export const isIgnoredInitialEvent = (event) => {
  return ignoredInitialMessageTypes.includes(event);
};