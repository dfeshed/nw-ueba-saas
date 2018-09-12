import Immutable from 'seamless-immutable';
import { test, module } from 'qunit';
import reducer from 'investigate-hosts/reducers/details/drivers/reducer';
import * as ACTION_TYPES from 'investigate-hosts/actions/types';
import { LIFECYCLE } from 'redux-pack';
import makePackAction from '../../../../helpers/make-pack-action';
import { driversData } from '../../../state/state';
import _ from 'lodash';

module('Unit | Reducers | drivers');

const initialState = {
  driver: null,
  driverLoadingStatus: null,
  selectedRowId: null,
  selectedDriverList: [],
  driverStatusData: {}
};

test('should return the initial state', function(assert) {
  const result = reducer(undefined, {});
  assert.deepEqual(result, initialState);
});


test('The RESET_HOST_DETAILS will reset the state', function(assert) {
  const previous = Immutable.from({
    driver: { 1: { path: '/root', fileProperties: { fileName: 'test' } } }
  });
  const result = reducer(previous, { type: ACTION_TYPES.RESET_HOST_DETAILS });

  assert.deepEqual(result, initialState);
});

test('The SET_DRIVERS_SELECTED_ROW will reset the state', function(assert) {
  const previous = Immutable.from({
    selectedRowId: null
  });
  const result = reducer(previous, { type: ACTION_TYPES.SET_DRIVERS_SELECTED_ROW, payload: { id: 123 } });

  assert.equal(result.selectedRowId, 123, 'Expected to match the selected id 123');
});

test('The FETCH_FILE_CONTEXT_DRIVERS sets the host details information', function(assert) {
  const previous = Immutable.from({
    driver: null,
    driverLoadingStatus: 'completed'
  });

  const startAction = makePackAction(LIFECYCLE.START, { type: ACTION_TYPES.FETCH_FILE_CONTEXT_DRIVERS });
  const startEndState = reducer(previous, startAction);
  assert.deepEqual(startEndState.driverLoadingStatus, 'wait');

  const action = makePackAction(LIFECYCLE.SUCCESS, {
    type: ACTION_TYPES.FETCH_FILE_CONTEXT_DRIVERS,
    payload: { data: driversData }
  });

  const endState = reducer(previous, action);
  assert.deepEqual(_.values(endState.driver).length, 4);
});
test('The HOST_DETAILS_DATATABLE_SORT_CONFIG resets the selected row id', function(assert) {
  const previous = Immutable.from({
    selectedRowId: '123'
  });
  const result = reducer(previous, { type: ACTION_TYPES.CHANGE_AUTORUNS_TAB });
  assert.equal(result.selectedFileId, null);
});
test('TOGGLE_SELECTED_DRIVER should toggle the selected driver', function(assert) {
  const previous = Immutable.from({
    selectedRowId: '123',
    selectedDriverList: []
  });
  const driver = {
    id: 0,
    checksumSha256: 0,
    signature: '',
    size: 0 };
  let result = reducer(previous, { type: ACTION_TYPES.TOGGLE_SELECTED_DRIVER, payload: driver });
  assert.equal(result.selectedDriverList.length, 1);
  assert.equal(result.selectedDriverList[0].id, 0);
  const next = Immutable.from({
    selectedRowId: '123',
    selectedDriverList: [driver]
  });
  result = reducer(next, { type: ACTION_TYPES.TOGGLE_SELECTED_DRIVER, payload: driver });
  assert.equal(result.selectedDriverList.length, 0);
});
test('TOGGLE_ALL_DRIVER_SELECTION should toggle the selected driver', function(assert) {
  const previous = Immutable.from({
    selectedRowId: '123',
    selectedDriverList: [],
    driver: {
      drivers_61: {
        id: '0'
      }
    }
  });
  const driver = {
    id: 0,
    checksumSha256: 0,
    signature: '',
    size: 0 };
  let result = reducer(previous, { type: ACTION_TYPES.TOGGLE_ALL_DRIVER_SELECTION });
  assert.equal(result.selectedDriverList.length, 1);
  assert.equal(result.selectedDriverList[0].id, 0);
  const next = Immutable.from({
    selectedRowId: '123',
    selectedDriverList: [driver]
  });
  result = reducer(next, { type: ACTION_TYPES.TOGGLE_ALL_DRIVER_SELECTION, payload: driver });
  assert.equal(result.selectedDriverList.length, 0);
});
test('The GET_DRIVER_STATUS set server response to state', function(assert) {
  const previous = Immutable.from({
    driverStatusData: {}
  });

  const startAction = makePackAction(LIFECYCLE.START, { type: ACTION_TYPES.GET_DRIVER_STATUS });
  const startEndState = reducer(previous, startAction);

  assert.deepEqual(startEndState.driverStatusData, {});

  const action = makePackAction(LIFECYCLE.SUCCESS, {
    type: ACTION_TYPES.GET_DRIVER_STATUS,
    payload: { data: [ { resultList: [ { data: 'Whitelist' } ] } ] }
  });
  const endState = reducer(previous, action);
  assert.equal(endState.driverStatusData, 'Whitelist');
});