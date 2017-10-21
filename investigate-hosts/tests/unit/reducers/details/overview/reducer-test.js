import Immutable from 'seamless-immutable';
import { test, module } from 'qunit';
import reducer from 'investigate-hosts/reducers/details/overview/reducer';
import * as ACTION_TYPES from 'investigate-hosts/actions/types';
import { LIFECYCLE } from 'redux-pack';
import makePackAction from '../../../../helpers/make-pack-action';

module('Unit | Reducers | overview');

const initialState = {
  hostDetails: null,
  downloadId: null,
  exportJSONStatus: 'completed'
};

test('should return the initial state', function(assert) {
  const result = reducer(undefined, {});
  assert.deepEqual(result, initialState);
});

test('The RESET_INPUT_DATA will reset the state', function(assert) {
  const previous = Immutable.from({
    hostDetails: { hostName: 'test' }
  });
  const result = reducer(previous, { type: ACTION_TYPES.RESET_INPUT_DATA });

  assert.deepEqual(result, initialState);
});

test('The FETCH_HOST_DETAILS sets the host details information', function(assert) {
  const previous = Immutable.from({
    hostDetails: null
  });
  const newAction = makePackAction(LIFECYCLE.SUCCESS, {
    type: ACTION_TYPES.FETCH_HOST_DETAILS,
    payload: { data: { hostName: 'test' } }
  });
  const newEndState = reducer(previous, newAction);
  assert.equal(newEndState.hostDetails.hostName, 'test');
});

test('The FETCH_DOWNLOAD_FILECONTEXT_JOB_ID download id', function(assert) {
  const previous = Immutable.from({
    downloadId: null
  });
  const newAction = makePackAction(LIFECYCLE.SUCCESS, {
    type: ACTION_TYPES.FETCH_DOWNLOAD_FILECONTEXT_JOB_ID,
    payload: { data: { id: 123123 } }
  });
  const newEndState = reducer(previous, newAction);
  assert.equal(newEndState.downloadId, 123123);
});
