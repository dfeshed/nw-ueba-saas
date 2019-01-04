import fetchMetaValue from 'investigate-shared/actions/api/events/meta-values';
import RSVP from 'rsvp';
import { lookup } from 'ember-dependency-lookup';
import { buildTimeRange } from 'investigate-shared/utils/time-util';
import { put, all, call, takeLatest, select } from 'redux-saga/effects';
import * as ACTION_TYPES from 'investigate-files/actions/types';
import _ from 'lodash';

const MAX_PENDING_QUERIES = 2; // SDK configuration, currently hardcoded for UI

const getMetaValues = ({ filter, queryNode, metaName, size = 1 }) => {
  return new RSVP.Promise((resolve, reject) => {
    const query = { ...queryNode };

    if (query.timeRange) {
      const { timeRange: { value, unit } } = query;
      const timeZone = lookup('service:timezone');
      const { zoneId } = timeZone.get('selected');
      const { startTime, endTime } = buildTimeRange(value, unit, zoneId);
      query.startTime = startTime;
      query.endTime = endTime;
    }

    query.metaFilter = {
      conditions: [
        {
          meta: 'device.type',
          operator: '=',
          value: '\'nwendpoint\''
        },
        filter
      ]
    };

    const handlers = {
      onError() {
        reject();
      },
      onResponse() {

      },
      onCompleted(response) {
        resolve(response.data);
      }
    };
    fetchMetaValue(query, metaName, size, null, 10000, 10000, handlers, 0);
  });
};

function* fetchHostNameList({ payload }) {
  const state = yield select();
  try {
    const queryNode = state.investigate;
    const { serviceId } = queryNode;
    if (serviceId && serviceId !== '-1') {
      const input = {
        queryNode,
        size: 300000,
        metaName: 'alias.host'

      };
      const childrenChunks = _.chunk(payload, MAX_PENDING_QUERIES);
      let finalResult = {};
      for (let i = 0; i < childrenChunks.length; i++) {
        put({ type: ACTION_TYPES.AGENT_COUNT_INIT, payload: childrenChunks[i] });
        const result = yield all(getAPICalls(input, childrenChunks[i]));
        finalResult = { ...finalResult, ...result };
      }
      const result2 = {};
      for (const key in finalResult) {
        result2[key] = finalResult[key].length;
      }
      yield put({ type: ACTION_TYPES.SET_AGENT_COUNT, payload: result2 });
    }
  } catch (e) {
    yield put({ type: ACTION_TYPES.SET_AGENT_COUNT_FAILED });
  }

}

const getAPICalls = (input, checksums) => {
  const apiCalls = checksums.reduce((result, checksum) => {
    input.filter = { value: `(checksum.all = '${checksum}')` };
    result[checksum] = call(
      getMetaValues,
      input
    );
    return result;
  }, {});
  return apiCalls;
};

export function* fetchValue() {
  yield takeLatest(ACTION_TYPES.GET_AGENTS_COUNT_SAGA, fetchHostNameList);
}

