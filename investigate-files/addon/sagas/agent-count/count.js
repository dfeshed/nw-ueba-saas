import { call, all, put, takeLatest, select } from 'redux-saga/effects';
import * as ACTION_TYPES from 'investigate-files/actions/types';
import fetchDistinctCount from 'investigate-shared/actions/api/events/event-count-distinct';
import _ from 'lodash';

const MAX_PENDING_QUERIES = 15; // SDK configuration, currently hardcoded for UI
/**
 * For each child process getting the children count.
 * Iterating over the list of children and invoking the fetchEventCount api for each children and waiting for all the
 * request to complete. Once request is complete updating the state with count and invoking the onComplete callBack
 *
 * Response payload will have following structure
 *  {
 *    checksum: <fetchEvent count response>
 *  }
 *
 *  ex:
 *  {
 *    12323131313123132: { data: 1 },
 *    12313131321231311: { data: 2 }
 *  }
 *
 *
 * @param action
 * @returns {IterableIterator<*>}
 * @public
 */
function* fetchAgentCountAsync({ payload }) {
  const state = yield select();
  try {
    const { serviceId, startTime, endTime } = state.investigateQuery;
    // If pending query exceeded the limit then SDK is throwing the error, to overcome the error, splitting the
    // children into chunks
    const childrenChunks = _.chunk(payload, MAX_PENDING_QUERIES);
    for (let i = 0; i < childrenChunks.length; i++) {
      const result = yield all(getAPICalls(serviceId, startTime, endTime, childrenChunks[i]));
      payload = { ...payload, ...result };
    }

    yield put({ type: ACTION_TYPES.SET_AGENT_COUNT, payload: _.mapValues(payload, 'data') });

  } catch (e) {
    yield put({ type: ACTION_TYPES.SET_AGENT_COUNT_FAILED });
  }
}

const getAPICalls = (serviceId, startTime, endTime, checksums) => {
  const apiCalls = checksums.reduce((result, checksum) => {
    const conditions = [
      {
        meta: 'device.type',
        operator: '=',
        value: '\'nwendpoint\''
      },
      { value: `(checksum = \'${checksum}\')` }
    ];
    // call api response will stored as key and value
    result[checksum] = call(
      fetchDistinctCount,
      'agent.id',
      serviceId,
      startTime,
      endTime,
      conditions,
      null,
      null,
      false
    );
    return result;
  }, {});
  return apiCalls;
};

export function* fetchAgentCount() {
  yield takeLatest(ACTION_TYPES.GET_AGENTS_COUNT_SAGA, fetchAgentCountAsync);
}


