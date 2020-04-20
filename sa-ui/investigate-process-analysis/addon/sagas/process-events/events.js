import { call, all, put, takeLatest, select, fork, join } from 'redux-saga/effects';
import * as ACTION_TYPES from 'investigate-process-analysis/actions/types';
import fetchDistinctCount from 'investigate-shared/actions/api/events/event-count-distinct';
import { getQueryNode, getMetaFilterFor } from 'investigate-process-analysis/actions/creators/util';
import { getLocalRiskScore } from 'investigate-process-analysis/actions/api/risk-score';

import _ from 'lodash';
import { getMetaValues } from 'investigate-process-analysis/actions/creators/events-creators';
import RSVP from 'rsvp';

const MAX_PENDING_QUERIES = 4; // SDK configuration, currently hardcoded for UI
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
    const queryNode = getQueryNode(queryInput);
    const { serviceId, startTime, endTime, agentId } = queryNode;
    let payload = {};

    // If pending query exceeded the limit then SDK is throwing the error, to overcome the error, splitting the
    // children into chunks
    const childrenChunks = _.chunk(children, MAX_PENDING_QUERIES);
    const riskScore = yield fork(fetchLocalRiskScore, agentId, children);
    const getCategory = yield fork(fetchEventCategory, children, queryNode);
    for (let i = 0; i < childrenChunks.length; i++) {
      const result = yield all(getAPICalls(serviceId, startTime, endTime, agentId, childrenChunks[i]));
      payload = { ...payload, ...result };
    }

    yield put({ type: ACTION_TYPES.SET_EVENTS_COUNT, payload });
    yield join(getCategory, riskScore);
    // Event loading is complete
    yield put({ type: ACTION_TYPES.COMPLETED_EVENTS_STREAMING });

    // Invoke the onComplete callBack which is responsible for rendering the tree
    onComplete(); // Invoke callback
  } catch (e) {
    yield put({ type: ACTION_TYPES.SET_EVENTS_COUNT_FAILED });
  }
}

function* fetchLocalRiskScore(agentId, children) {
  // If children are empty don't make the api call
  if (!children || !children.length) {
    return;
  }
  try {
    const { data } = yield call(getLocalRiskScore, agentId, _getCheckSums(children));
    yield put({ type: ACTION_TYPES.SET_LOCAL_RISK_SCORE, payload: { score: data } });
  } catch (e) {
    yield put({ type: ACTION_TYPES.SET_LOCAL_RISK_SCORE, payload: { score: null } });
  }
}

function* fetchEventCategory(children, query) {
  for (let i = 0; i < children.length; i++) {
    const pid = children[i].processId;
    const data = yield call(getEventCategory, pid, query);
    yield put({ type: ACTION_TYPES.SET_EVENT_CATEGORY, payload: { pid, eventCategory: data } });
  }
}

const getEventCategory = (pid, query) => {
  const hasNetwork = _hasCategory(query, 'Network Event', pid);
  const hasFile = _hasCategory(query, 'File Event', pid);
  const hasRegistry = _hasCategory(query, 'Registry Event', pid);
  return RSVP.all([hasNetwork, hasFile, hasRegistry]);
};

const _hasCategory = (query, category, pid) => {
  const { serviceId, startTime, endTime, agentId } = query;
  return getMetaValues({
    serviceId,
    startTime,
    endTime,
    metaName: 'process.vid.src',
    filter: [{ value: `(agent.id = '${agentId}' && category = '${category}' && process.vid.src = '${pid}')` }]
  });
};

const _getCheckSums = (children) => {
  return children.mapBy('checksumDst').uniq();
};

const getAPICalls = (serviceId, startTime, endTime, agentId, children) => {
  const apiCalls = children.reduce((result, child) => {
    const { conditions } = getMetaFilterFor('CHILD', agentId, child.processId);
    // call api response will stored as key and value
    result[child.processId] = call(
      fetchDistinctCount,
      'process.vid.dst',
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


export function* fetchEventsCount() {
  yield takeLatest(ACTION_TYPES.GET_EVENTS_COUNT_SAGA, fetchEventsCountAsync);
}


