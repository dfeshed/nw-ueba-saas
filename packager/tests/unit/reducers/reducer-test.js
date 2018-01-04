import Immutable from 'seamless-immutable';
import { test, module } from 'qunit';
import reducer from 'packager/reducers/packager';
import * as ACTION_TYPES from 'packager/actions/types';
import data from '../../data/subscriptions/packageconfig/get/data';
import fieldsData from '../../data/subscriptions/packageconfig/updateFields/data';
import { LIFECYCLE } from 'redux-pack';
import makePackAction from '../../helpers/make-pack-action';
module('Unit | Reducers | Packager');

const initialState = Immutable.from({
  defaultPackagerConfig: {},
  error: null,
  loading: false,
  downloadLink: null,
  updating: false,
  initialState: {},
  devices: {}
});

test('should return the initial state', function(assert) {
  const result = reducer(undefined, {});
  assert.deepEqual(result, initialState);
});


test('should reset the form, defaultPackagerConfig to previous state value', function(assert) {
  const result = reducer(undefined, { type: ACTION_TYPES.RESET_FORM });
  assert.deepEqual(result, initialState);
});

test('should retrieve failure', function(assert) {
  const previous = Immutable.from({
    error: false,
    loading: true
  });
  const result = reducer(previous, { type: ACTION_TYPES.RETRIEVE_FAILURE });
  assert.equal(result.error, true, 'error is set to true');
  assert.equal(result.loading, false, 'loading is set to false');
});

test('Get defaultPackagerConfig ', function(assert) {
  const previous = Immutable.from({
    defaultPackagerConfig: {},
    error: false,
    loading: false,
    initialState: {}
  });
  const action = makePackAction(LIFECYCLE.SUCCESS, {
    type: ACTION_TYPES.GET_INFO,
    payload: { data }
  });

  const endState = reducer(previous, action);
  assert.deepEqual(endState.defaultPackagerConfig, data);
  assert.deepEqual(endState.initialState, data);
});

test('Update Redux state with UI state ', function(assert) {
  const previous = Immutable.from({
    defaultPackagerConfig: {}
  });
  const action = {
    type: ACTION_TYPES.UPDATE_FIELDS,
    payload: fieldsData
  };
  const endState = reducer(previous, action);
  assert.deepEqual(endState.defaultPackagerConfig, fieldsData);
});