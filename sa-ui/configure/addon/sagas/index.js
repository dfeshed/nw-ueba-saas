import { fork } from 'redux-saga/effects';
import {
  fetchRules,
  fetchRule,
  fetchFields,
  deleteRule,
  enableRules,
  disableRules,
  reorderRules,
  cloneRule,
  saveRule,
  createRule
} from './incident-rules/rules';

import {
  fetchNotificationSettings,
  updateNotificationSettings
} from './respond-notifications/notifications';

import {
  fetchRiskScoringSettings,
  updateRiskScoringSettings
} from './risk-scoring/settings';

export default function* root() {
  yield[
    fork(fetchRules),
    fork(fetchRule),
    fork(fetchFields),
    fork(deleteRule),
    fork(enableRules),
    fork(disableRules),
    fork(reorderRules),
    fork(cloneRule),
    fork(saveRule),
    fork(createRule),
    fork(fetchNotificationSettings),
    fork(updateNotificationSettings),
    fork(fetchRiskScoringSettings),
    fork(updateRiskScoringSettings)
  ];
}
