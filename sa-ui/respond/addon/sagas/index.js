import { fork } from 'redux-saga/effects';
import { createIncident } from './alerts/incidents';
import { fetchLanguagesAndAliases } from './recon/dictionaries';

export default function* root() {
  yield[
    fork(createIncident),
    fork(fetchLanguagesAndAliases)
  ];
}
