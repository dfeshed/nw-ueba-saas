import { fork } from 'redux-saga/effects';
import { fetchEventsCount } from './process-events/events';

export default function* root() {
  yield[
    fork(fetchEventsCount)
  ];
}
