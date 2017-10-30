import * as ACTION_TYPES from 'respond/actions/types';
import * as errorHandlers from 'respond/actions/util/error-handlers';
import { aggregationRules } from 'respond/actions/api';
import { call, put, takeLatest } from 'redux-saga/effects';
import { success, failure } from 'respond/sagas/flash-messages';
import Ember from 'ember';

const { Logger } = Ember;

function* fetchRulesAsync() {
  try {
    yield put({ type: ACTION_TYPES.FETCH_AGGREGATION_RULES_STARTED });
    const payload = yield call(aggregationRules.getAggregationRules);
    yield put({ type: ACTION_TYPES.FETCH_AGGREGATION_RULES, payload });
    Logger.debug(ACTION_TYPES.FETCH_AGGREGATION_RULES, payload);
  } catch (e) {
    yield put({ type: ACTION_TYPES.FETCH_AGGREGATION_RULES_FAILED });
    errorHandlers.handleContentRetrievalError(e, 'fetch aggregation rules');
  }
}


function* deleteRuleAsync(action) {
  const { ruleId } = action;
  try {
    yield put({ type: ACTION_TYPES.AGGREGATION_RULES_DELETE_STARTED });
    const payload = yield call(aggregationRules.deleteAggregationRule, ruleId);
    yield put({ type: ACTION_TYPES.AGGREGATION_RULES_DELETE, payload });
    success('respond.entities.actionMessages.deleteSuccess');
    Logger.debug(ACTION_TYPES.AGGREGATION_RULES_DELETE, payload);
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
    Logger.debug(ACTION_TYPES.AGGREGATION_RULES_REORDER, payload);
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
    Logger.debug(ACTION_TYPES.AGGREGATION_RULES_CLONE, payload);
  } catch (e) {
    yield put({ type: ACTION_TYPES.AGGREGATION_RULES_CLONE_FAILED });
    failure('respond.entities.actionMessages.createFailure');
    errorHandlers.handleContentCreationError(e, `clone of rule ${templateRuleId}`);
  }
}

export function* fetchRules() {
  yield takeLatest(ACTION_TYPES.FETCH_AGGREGATION_RULES_SAGA, fetchRulesAsync);
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
