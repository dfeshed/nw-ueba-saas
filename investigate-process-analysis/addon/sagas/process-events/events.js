import { call, all, put, takeLatest, select } from 'redux-saga/effects';
import * as ACTION_TYPES from 'investigate-process-analysis/actions/types';
import fetchEventCount from 'investigate-shared/actions/api/investigate-events/event-count';
import { getQueryNode } from 'investigate-process-analysis/actions/creators/util';

function* fetchEventsCountAsync(action) {
  const state = yield select();
  const { onComplete } = action;
  try {
    const { queryInput, rawData: children } = state.processAnalysis.processTree;

    const apiCalls = children.reduce((result, child) => {
      const queryNode = getQueryNode(queryInput, child.processName);
      const { serviceId, startTime, endTime, metaFilter } = queryNode;
      result[child.processName] = call(fetchEventCount, serviceId, startTime, endTime, metaFilter.conditions, null, null, false);
      return result;
    }, {});
    const payload = yield all(apiCalls);
    yield put({ type: ACTION_TYPES.SET_EVENTS_COUNT, payload });
    yield put({ type: ACTION_TYPES.COMPLETED_EVENTS_STREAMING });
    onComplete(); // Invoke callback
  } catch (e) {
    yield put({ type: ACTION_TYPES.SET_EVENTS_COUNT_FAILED });
  }
}


export function* fetchEventsCount() {
  yield takeLatest(ACTION_TYPES.GET_EVENTS_COUNT_SAGA, fetchEventsCountAsync);
}


