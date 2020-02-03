import { test, module } from 'qunit';
import reducer from 'recon/reducers/data-reducer';
import * as ACTION_TYPES from 'recon/actions/types';
import Immutable from 'seamless-immutable';
import { LIFECYCLE } from 'redux-pack';
import makePackAction from '../../helpers/make-pack-action';
import moment from 'moment';

module('Unit | Reducers | data-reducer | Recon');

const initialState = Immutable.from({
  eventId: null,
  apiFatalErrorCode: 0
});

test('test SET_FATAL_API_ERROR_FLAG action handler', function(assert) {
  const action = {
    type: ACTION_TYPES.SET_FATAL_API_ERROR_FLAG,
    payload: 124
  };
  const result = reducer(initialState, action);

  assert.equal(result.apiFatalErrorCode, 124);
});

test('META_RETRIEVE success will set queryInput start & end times to meta collection time if isStandalone', function(assert) {
  const isStandAlone = Immutable.from({
    queryInputs: {
      startTime: 123,
      endTime: 420
    },
    isStandalone: true
  });
  const action = makePackAction(LIFECYCLE.SUCCESS, {
    type: ACTION_TYPES.META_RETRIEVE,
    payload: [['time', '2019-07-16T09:37:24.000+0000' ]]
  });
  const collectionStTime = moment('2019-07-16T09:37:24.000+0000');
  const collectionEnTime = collectionStTime.clone();
  const expectedStartTime = collectionStTime.subtract(15, 'minutes').unix();
  const expectedEndTime = collectionEnTime.add(15, 'minutes').unix();

  const result = reducer(isStandAlone, action);

  assert.equal(result.queryInputs.startTime, expectedStartTime);
  assert.equal(result.queryInputs.endTime, expectedEndTime);
});

test('META_RETRIEVE success will not mess with queryInput start & end times if not in isStandalone', function(assert) {
  const action = makePackAction(LIFECYCLE.SUCCESS, {
    type: ACTION_TYPES.META_RETRIEVE,
    payload: [['time', '2019-07-16T09:37:24.000+0000' ]]
  });

  const result = reducer(Immutable.from({
    queryInputs: {
      startTime: 123,
      endTime: 420
    },
    isStandalone: false
  }), action);

  assert.equal(result.queryInputs.startTime, 123);
  assert.equal(result.queryInputs.endTime, 420);
});

test('test CLOSE_RECON', function(assert) {
  const dataInitialState = Immutable.from({
    // Recon inputs
    endpointId: null,
    eventId: null,
    eventType: null,
    contentError: null, // handler for content related errors
    contentLoading: false,
    isStandalone: false,
    apiFatalErrorCode: 0, // handler for shutting down recon and displaying error
    contextMenuItems: [],
    queryInputs: null,
    index: undefined,
    total: undefined
  });

  const currentState = dataInitialState.merge({
    endpointId: '1',
    eventId: '2',
    eventType: 'NETWORK',
    queryInputs: { foo: 'bar' },
    index: 1,
    total: 500
  });

  const action = {
    type: ACTION_TYPES.CLOSE_RECON
  };
  const result = reducer(currentState, action);
  assert.deepEqual(result, dataInitialState);
});
