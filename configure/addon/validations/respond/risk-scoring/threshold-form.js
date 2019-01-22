import { get } from '@ember/object';
import { maybeValidate } from 'configure/validations/maybe';
import { localizeMessage } from 'configure/validations/localize';
import { validateNumber, validatePresence } from 'ember-changeset-validations/validators';

const fileEnabled = 'file.enabled';
const hostEnabled = 'host.enabled';
const context = 'configure.incidentRules.riskScoring.validations';

const enabled = (value) => ![null, undefined].includes(value);
const isEnabled = function(changes, content) {
  const newValue = get(changes, this);
  const oldValue = get(content, this);
  return enabled(newValue) ? newValue : oldValue;
};

export default {
  'file.enabled': validatePresence(true),
  'file.threshold': maybeValidate(validateNumber({ gte: 0, lte: 100, message: localizeMessage('threshold', context) }), isEnabled.bind(fileEnabled)),
  'file.timeWindow': maybeValidate(validateNumber({ gte: 1, lte: 24, message: localizeMessage('timeWindow', context) }), isEnabled.bind(fileEnabled)),
  'file.timeWindowUnit': maybeValidate(validatePresence({ presence: true, message: localizeMessage('timeWindowUnit', context) }), isEnabled.bind(fileEnabled)),
  'host.enabled': validatePresence(true),
  'host.threshold': maybeValidate(validateNumber({ gte: 0, lte: 100, message: localizeMessage('threshold', context) }), isEnabled.bind(hostEnabled)),
  'host.timeWindow': maybeValidate(validateNumber({ gte: 1, lte: 24, message: localizeMessage('timeWindow', context) }), isEnabled.bind(hostEnabled)),
  'host.timeWindowUnit': maybeValidate(validatePresence({ presence: true, message: localizeMessage('timeWindowUnit', context) }), isEnabled.bind(hostEnabled))
};
