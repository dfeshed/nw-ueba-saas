import * as ACTION_TYPES from 'configure/actions/types/respond';
import respondAPI from 'configure/actions/api/respond';
import { call, put, takeLatest } from 'redux-saga/effects';
import { success, failure } from 'configure/sagas/flash-messages';

const { riskScoring } = respondAPI;

function* fetchRiskScoringSettingsAsync() {
  try {
    yield put({ type: ACTION_TYPES.FETCH_RISK_SCORING_SETTINGS_STARTED });
    const payload = yield call(riskScoring.fetchRiskScoringSettings);
    yield put({ type: ACTION_TYPES.FETCH_RISK_SCORING_SETTINGS, payload });
  } catch (e) {
    yield put({ type: ACTION_TYPES.FETCH_RISK_SCORING_SETTINGS_FAILED });
  }
}

function* updateRiskScoringSettingsAsync(action) {
  const { riskScoringSettings } = action;
  try {
    yield put({ type: ACTION_TYPES.UPDATE_RISK_SCORING_SETTINGS_STARTED });
    const payload = yield call(riskScoring.updateRiskScoringSettings, riskScoringSettings);
    yield put({ type: ACTION_TYPES.UPDATE_RISK_SCORING_SETTINGS, payload });
    success('configure.incidentRules.riskScoring.actionMessages.updateSuccess');
  } catch (e) {
    yield put({ type: ACTION_TYPES.UPDATE_RISK_SCORING_SETTINGS_FAILED });
    failure('configure.incidentRules.riskScoring.actionMessages.updateFailure');
  }
}

export function* fetchRiskScoringSettings() {
  yield takeLatest(ACTION_TYPES.FETCH_RISK_SCORING_SETTINGS_SAGA, fetchRiskScoringSettingsAsync);
}

export function* updateRiskScoringSettings() {
  yield takeLatest(ACTION_TYPES.UPDATE_RISK_SCORING_SETTINGS_SAGA, updateRiskScoringSettingsAsync);
}
