import * as ACTION_TYPES from 'respond/actions/types';
import * as errorHandlers from 'respond/actions/util/error-handlers';
import { aggregationRules } from 'respond/actions/api';
import { call, put, takeLatest } from 'redux-saga/effects';
import { success, failure } from 'respond/sagas/flash-messages';

function* fetchRulesAsync() {
  try {
    yield put({ type: ACTION_TYPES.FETCH_AGGREGATION_RULES_STARTED });
    const payload = yield call(aggregationRules.getAggregationRules);
    yield put({ type: ACTION_TYPES.FETCH_AGGREGATION_RULES, payload });
  } catch (e) {
    yield put({ type: ACTION_TYPES.FETCH_AGGREGATION_RULES_FAILED });
    errorHandlers.handleContentRetrievalError(e, 'fetch aggregation rules');
  }
}

function* fetchRuleAsync(action) {
  const { ruleId } = action;
  try {
    yield put({ type: ACTION_TYPES.FETCH_AGGREGATION_RULE_STARTED });
    const payload = yield call(aggregationRules.getAggregationRule, ruleId);
    yield put({ type: ACTION_TYPES.FETCH_AGGREGATION_RULE, payload });
  } catch (e) {
    yield put({ type: ACTION_TYPES.FETCH_AGGREGATION_RULE_FAILED });
    errorHandlers.handleContentRetrievalError(e, `fetch aggregation rule ${ruleId}`);
  }
}

function* fetchFieldsAsync() {
  try {
    yield put({ type: ACTION_TYPES.FETCH_AGGREGATION_FIELDS_STARTED });
    const payload = yield call(aggregationRules.getAggregationFields);
    yield put({ type: ACTION_TYPES.FETCH_AGGREGATION_FIELDS, payload });
  } catch (e) {
    yield put({ type: ACTION_TYPES.FETCH_AGGREGATION_FIELDS_FAILED });
    errorHandlers.handleContentRetrievalError(e, 'fetch aggregation fields');
  }
}

function* deleteRuleAsync(action) {
  const { ruleId } = action;
  try {
    yield put({ type: ACTION_TYPES.AGGREGATION_RULES_DELETE_STARTED });
    const payload = yield call(aggregationRules.deleteAggregationRule, ruleId);
    yield put({ type: ACTION_TYPES.AGGREGATION_RULES_DELETE, payload });
    success('respond.entities.actionMessages.deleteSuccess');
  } catch (e) {
    yield put({ type: ACTION_TYPES.AGGREGATION_RULES_DELETE_FAILED });
    failure('respond.entities.actionMessages.deleteFailure');
    errorHandlers.handleContentDeletionError(e, `delete aggregation rule ${ruleId}`);
  }
}

function* reorderRulesAsync(action) {
  const { ruleIds } = action;
  try {
    yield put({ type: ACTION_TYPES.AGGREGATION_RULES_REORDER_STARTED });
    const payload = yield call(aggregationRules.reorderAggregationRules, ruleIds);
    yield put({ type: ACTION_TYPES.AGGREGATION_RULES_REORDER, payload });
    success('respond.entities.actionMessages.updateSuccess');
  } catch (e) {
    yield put({ type: ACTION_TYPES.AGGREGATION_RULES_REORDER_FAILED });
    failure('respond.entities.actionMessages.updateFailure');
    errorHandlers.handleContentUpdateError(e, 'reorder aggregation rules');
  }
}

function* cloneRuleAsync(action) {
  const { templateRuleId, onSuccess } = action;
  try {
    yield put({ type: ACTION_TYPES.AGGREGATION_RULES_CLONE_STARTED });
    const payload = yield call(aggregationRules.cloneAggregationRule, templateRuleId);
    yield put({ type: ACTION_TYPES.AGGREGATION_RULES_CLONE, payload });
    onSuccess(payload.data.id);
  } catch (e) {
    yield put({ type: ACTION_TYPES.AGGREGATION_RULES_CLONE_FAILED });
    failure('respond.entities.actionMessages.createFailure');
    errorHandlers.handleContentCreationError(e, `clone of rule ${templateRuleId}`);
  }
}

function* saveRuleAsync(action) {
  const { ruleInfo, onSuccess } = action;
  try {
    yield put({ type: ACTION_TYPES.AGGREGATION_RULES_SAVE_STARTED });
    const payload = yield call(aggregationRules.saveAggregationRule, ruleInfo);
    yield put({ type: ACTION_TYPES.AGGREGATION_RULES_SAVE, payload });
    success('respond.entities.actionMessages.saveSuccess');
    onSuccess();
  } catch (e) {
    yield put({ type: ACTION_TYPES.AGGREGATION_RULES_SAVE_FAILED });
    failure('respond.entities.actionMessages.saveFailure');
    errorHandlers.handleContentUpdateError(e, `update aggregation rule ${ruleInfo.ruleId}`);
  }
}

function* createRuleAsync(action) {
  const { ruleInfo, onSuccess } = action;
  try {
    yield put({ type: ACTION_TYPES.CREATE_AGGREGATION_RULE_STARTED });
    const payload = yield call(aggregationRules.createAggregationRule, ruleInfo);
    yield put({ type: ACTION_TYPES.CREATE_AGGREGATION_RULE, payload });
    success('respond.entities.actionMessages.saveSuccess');
    onSuccess();
  } catch (e) {
    yield put({ type: ACTION_TYPES.CREATE_AGGREGATION_RULE_FAILED });
    failure('respond.entities.actionMessages.saveFailure');
    errorHandlers.handleContentUpdateError(e, `update aggregation rule ${ruleInfo.ruleId}`);
  }
}

export function* fetchRules() {
  yield takeLatest(ACTION_TYPES.FETCH_AGGREGATION_RULES_SAGA, fetchRulesAsync);
}

export function* fetchRule() {
  yield takeLatest(ACTION_TYPES.FETCH_AGGREGATION_RULE_SAGA, fetchRuleAsync);
}

export function* fetchFields() {
  yield takeLatest(ACTION_TYPES.FETCH_AGGREGATION_FIELDS_SAGA, fetchFieldsAsync);
}

export function* deleteRule() {
  yield takeLatest(ACTION_TYPES.AGGREGATION_RULES_DELETE_SAGA, deleteRuleAsync);
}

export function* reorderRules() {
  yield takeLatest(ACTION_TYPES.AGGREGATION_RULES_REORDER_SAGA, reorderRulesAsync);
}

export function* cloneRule() {
  yield takeLatest(ACTION_TYPES.AGGREGATION_RULES_CLONE_SAGA, cloneRuleAsync);
}

export function* saveRule() {
  yield takeLatest(ACTION_TYPES.SAVE_AGGREGATION_RULE_SAGA, saveRuleAsync);
}

export function* createRule() {
  yield takeLatest(ACTION_TYPES.CREATE_AGGREGATION_RULE_SAGA, createRuleAsync);
}

