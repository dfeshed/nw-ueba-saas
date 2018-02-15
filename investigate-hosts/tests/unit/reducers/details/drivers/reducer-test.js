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
  selectedRowId: null
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
