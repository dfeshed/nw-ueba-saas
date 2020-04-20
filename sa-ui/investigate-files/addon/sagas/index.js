import { fork } from 'redux-saga/effects';
import { fetchValue } from './meta-value/value';

export default function* root() {
  yield[
    fork(fetchValue)
  ];
}
