import { call, all, put, takeLatest, select } from 'redux-saga/effects';
import * as ACTION_TYPES from 'investigate-process-analysis/actions/types';
import fetchEventCount from 'investigate-shared/actions/api/events/event-count';
import { getQueryNode } from 'investigate-process-analysis/actions/creators/util';

/**
 * For each child process getting the children count.
 * Iterating over the list of children and invoking the fetchEventCount api for each children and waiting for all the
 * request to complete. Once request is complete updating the state with count and invoking the onComplete callBack
 *
 * Response payload will have following structure
 *  {
 *    <process name>: <fetchEvent count response>
 *  }
 *
 *  ex:
 *  {
 *    test.exe: { data: 1 },
 *    evil.exe: { data: 2 }
 *  }
 *
 *
 * @param action
 * @returns {IterableIterator<*>}
 * @public
 */
function* fetchEventsCountAsync(action) {
  const state = yield select();
  const { onComplete } = action;
  try {
    const { queryInput, rawData: children } = state.processAnalysis.processTree;

    const apiCalls = children.reduce((result, child) => {
      const queryNode = getQueryNode(queryInput, child.processId);
      const { serviceId, startTime, endTime, metaFilter } = queryNode;
      // call api response will stored as key and value
      result[child.processId] = call(fetchEventCount, serviceId, startTime, endTime, metaFilter.conditions, null, null, false);

      return result;
    }, {});

    const payload = yield all(apiCalls);
    yield put({ type: ACTION_TYPES.SET_EVENTS_COUNT, payload });
    // Event loading is complete
    yield put({ type: ACTION_TYPES.COMPLETED_EVENTS_STREAMING });

    // Invoke the onComplete callBack which is responsible for rendering the tree
    onComplete(); // Invoke callback
  } catch (e) {
    yield put({ type: ACTION_TYPES.SET_EVENTS_COUNT_FAILED });
  }
}


export function* fetchEventsCount() {
  yield takeLatest(ACTION_TYPES.GET_EVENTS_COUNT_SAGA, fetchEventsCountAsync);
}


