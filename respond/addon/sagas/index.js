import { fork } from 'redux-saga/effects';
import { createIncident } from './alerts/incidents';
import { fetchRules, deleteRule, reorderRules, cloneRule } from './aggregation-rules/rules';

export default function* root() {
  yield [
    fork(createIncident),
    fork(fetchRules),
    fork(deleteRule),
    fork(reorderRules),
    fork(cloneRule)
  ];
}
