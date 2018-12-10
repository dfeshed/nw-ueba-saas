import { localizeMessage } from 'configure/validations/localize';
import { validateNumber, validatePresence } from 'ember-changeset-validations/validators';

const context = 'configure.incidentRules.riskScoring.validations';

export default {
  'file.threshold': validateNumber({ gte: 0, lte: 100, message: localizeMessage('threshold', context) }),
  'file.timeWindow': validateNumber({ gte: 1, lte: 24, message: localizeMessage('timeWindow', context) }),
  'file.timeWindowUnit': validatePresence({ presence: true, message: localizeMessage('timeWindowUnit', context) }),
  'host.threshold': validateNumber({ gte: 0, lte: 100, message: localizeMessage('threshold', context) }),
  'host.timeWindow': validateNumber({ gte: 1, lte: 24, message: localizeMessage('timeWindow', context) }),
  'host.timeWindowUnit': validatePresence({ presence: true, message: localizeMessage('timeWindowUnit', context) })
};
