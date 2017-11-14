import { fork } from 'redux-saga/effects';
import { createIncident } from './alerts/incidents';
import {
  fetchRules,
  fetchRule,
  fetchFields,
  deleteRule,
  reorderRules,
  cloneRule,
  saveRule,
  createRule
} from './aggregation-rules/rules';

export default function* root() {
  yield [
    fork(createIncident),
    fork(fetchRules),
    fork(fetchRule),
    fork(fetchFields),
    fork(deleteRule),
    fork(reorderRules),
    fork(cloneRule),
    fork(saveRule),
    fork(createRule)
  ];
}
