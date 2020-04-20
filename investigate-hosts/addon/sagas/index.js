import { fork } from 'redux-saga/effects';
import { fetchAgentCount } from './agent-count/count';

export default function* root() {
  yield[
    fork(fetchAgentCount)
  ];
}
