import { fork } from 'redux-saga/effects';
import {
  fetchRules,
  fetchRule,
  fetchFields,
  deleteRule,
  reorderRules,
  cloneRule,
  saveRule,
  createRule
} from './incident-rules/rules';

export default function* root() {
  yield [
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
