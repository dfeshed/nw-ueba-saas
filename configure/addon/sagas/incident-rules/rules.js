import * as ACTION_TYPES from 'configure/actions/types/respond';
import respondAPI from 'configure/actions/api/respond';
import { call, put, takeLatest } from 'redux-saga/effects';
import { success, failure } from 'configure/sagas/flash-messages';

const { incidentRules } = respondAPI;

function* fetchRulesAsync() {
  try {
    yield put({ type: ACTION_TYPES.FETCH_INCIDENT_RULES_STARTED });
    const payload = yield call(incidentRules.getIncidentRules);
    yield put({ type: ACTION_TYPES.FETCH_INCIDENT_RULES, payload });
  } catch (e) {
    yield put({ type: ACTION_TYPES.FETCH_INCIDENT_RULES_FAILED });
  }
}

function* fetchRuleAsync(action) {
  const { ruleId } = action;
  try {
    yield put({ type: ACTION_TYPES.FETCH_INCIDENT_RULE_STARTED });
    const payload = yield call(incidentRules.getIncidentRule, ruleId);
    yield put({ type: ACTION_TYPES.FETCH_INCIDENT_RULE, payload });
  } catch (e) {
    yield put({ type: ACTION_TYPES.FETCH_INCIDENT_RULE_FAILED });
  }
}

function* fetchFieldsAsync() {
  try {
    yield put({ type: ACTION_TYPES.FETCH_INCIDENT_FIELDS_STARTED });
    const payload = yield call(incidentRules.getIncidentFields);
    yield put({ type: ACTION_TYPES.FETCH_INCIDENT_FIELDS, payload });
  } catch (e) {
    yield put({ type: ACTION_TYPES.FETCH_INCIDENT_FIELDS_FAILED });
  }
}

function* deleteRuleAsync(action) {
  const { ruleId } = action;
  try {
    yield put({ type: ACTION_TYPES.INCIDENT_RULES_DELETE_STARTED });
    const payload = yield call(incidentRules.deleteIncidentRule, ruleId);
    yield put({ type: ACTION_TYPES.INCIDENT_RULES_DELETE, payload });
    success('configure.incidentRules.actionMessages.deleteSuccess');
  } catch (e) {
    yield put({ type: ACTION_TYPES.INCIDENT_RULES_DELETE_FAILED });
    failure('configure.incidentRules.actionMessages.deleteFailure');
  }
}

function* reorderRulesAsync(action) {
  const { ruleIds } = action;
  try {
    yield put({ type: ACTION_TYPES.INCIDENT_RULES_REORDER_STARTED });
    const payload = yield call(incidentRules.reorderIncidentRules, ruleIds);
    yield put({ type: ACTION_TYPES.INCIDENT_RULES_REORDER, payload });
    success('configure.incidentRules.actionMessages.reorderSuccess');
  } catch (e) {
    yield put({ type: ACTION_TYPES.INCIDENT_RULES_REORDER_FAILED });
    failure('configure.incidentRules.actionMessages.reorderFailure');
  }
}

function* cloneRuleAsync(action) {
  const { templateRuleId, onSuccess } = action;
  try {
    yield put({ type: ACTION_TYPES.INCIDENT_RULES_CLONE_STARTED });
    const payload = yield call(incidentRules.cloneIncidentRule, templateRuleId);
    yield put({ type: ACTION_TYPES.INCIDENT_RULES_CLONE, payload });
    success('configure.incidentRules.actionMessages.cloneSuccess');
    onSuccess(payload.data.id);
  } catch (e) {
    yield put({ type: ACTION_TYPES.INCIDENT_RULES_CLONE_FAILED });
    failure('configure.incidentRules.actionMessages.cloneFailure');
  }
}

function* saveRuleAsync(action) {
  const { ruleInfo, onSuccess } = action;
  try {
    yield put({ type: ACTION_TYPES.INCIDENT_RULES_SAVE_STARTED });
    const payload = yield call(incidentRules.saveIncidentRule, ruleInfo);
    yield put({ type: ACTION_TYPES.INCIDENT_RULES_SAVE, payload });
    success('configure.incidentRules.actionMessages.saveSuccess');
    onSuccess();
  } catch (e) {
    yield put({ type: ACTION_TYPES.INCIDENT_RULES_SAVE_FAILED });
    failure('configure.incidentRules.actionMessages.saveFailure');
  }
}

function* createRuleAsync(action) {
  const { ruleInfo, onSuccess } = action;
  try {
    yield put({ type: ACTION_TYPES.CREATE_INCIDENT_RULE_STARTED });
    const payload = yield call(incidentRules.createIncidentRule, ruleInfo);
    yield put({ type: ACTION_TYPES.CREATE_INCIDENT_RULE, payload });
    success('configure.incidentRules.actionMessages.createSuccess');
    onSuccess();
  } catch (e) {
    yield put({ type: ACTION_TYPES.CREATE_INCIDENT_RULE_FAILED });
    failure('configure.incidentRules.actionMessages.createFailure');
  }
}

export function* fetchRules() {
  yield takeLatest(ACTION_TYPES.FETCH_INCIDENT_RULES_SAGA, fetchRulesAsync);
}

export function* fetchRule() {
  yield takeLatest(ACTION_TYPES.FETCH_INCIDENT_RULE_SAGA, fetchRuleAsync);
}

export function* fetchFields() {
  yield takeLatest(ACTION_TYPES.FETCH_INCIDENT_FIELDS_SAGA, fetchFieldsAsync);
}

export function* deleteRule() {
  yield takeLatest(ACTION_TYPES.INCIDENT_RULES_DELETE_SAGA, deleteRuleAsync);
}

export function* reorderRules() {
  yield takeLatest(ACTION_TYPES.INCIDENT_RULES_REORDER_SAGA, reorderRulesAsync);
}

export function* cloneRule() {
  yield takeLatest(ACTION_TYPES.INCIDENT_RULES_CLONE_SAGA, cloneRuleAsync);
}

export function* saveRule() {
  yield takeLatest(ACTION_TYPES.SAVE_INCIDENT_RULE_SAGA, saveRuleAsync);
}

export function* createRule() {
  yield takeLatest(ACTION_TYPES.CREATE_INCIDENT_RULE_SAGA, createRuleAsync);
}

