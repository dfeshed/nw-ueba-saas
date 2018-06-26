import * as ACTION_TYPES from 'respond/actions/types';
import { call, put, select, takeLatest } from 'redux-saga/effects';
import { recon } from 'respond/actions/api';

const getReconState = (state) => state.respond.recon;

function* fetchFromCacheOrWebSocket(endpointId) {
  const { aliasesCache, languageCache } = yield select(getReconState);
  if (aliasesCache[endpointId] && languageCache[endpointId]) {
    yield put({ type: ACTION_TYPES.GET_FROM_LANGUAGE_AND_ALIASES_CACHE, payload: { endpointId } });
  } else {
    yield put({ type: ACTION_TYPES.ALIASES_AND_LANGUAGE_RETRIEVE, payload: { loading: true } });
    const response = yield call(recon.getLanguagesAndAliases, endpointId);
    yield put({ type: ACTION_TYPES.ALIASES_AND_LANGUAGE_COMPLETE, payload: { response, endpointId } });
  }
}

function* fetchLanguagesAndAliasesAsync(action) {
  try {
    const { endpointId, selection } = action;
    yield call(fetchFromCacheOrWebSocket, endpointId);
    yield put({ type: ACTION_TYPES.SET_INCIDENT_SELECTION, payload: { type: 'event', id: selection } });
  } finally {
    yield put({ type: ACTION_TYPES.ALIASES_AND_LANGUAGE_RETRIEVE, payload: { loading: false } });
  }
}

export function* fetchLanguagesAndAliases() {
  yield takeLatest(ACTION_TYPES.ALIASES_AND_LANGUAGE_RETRIEVE_SAGA, fetchLanguagesAndAliasesAsync);
}
