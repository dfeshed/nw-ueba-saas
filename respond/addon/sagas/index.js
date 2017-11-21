import { fork } from 'redux-saga/effects';
import { createIncident } from './alerts/incidents';

export default function* root() {
  yield [
    fork(createIncident)
  ];
}
