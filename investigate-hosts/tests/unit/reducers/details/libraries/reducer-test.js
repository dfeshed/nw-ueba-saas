import Immutable from 'seamless-immutable';
import { test, module } from 'qunit';
import reducer from 'investigate-hosts/reducers/details/libraries/reducer';
import * as ACTION_TYPES from 'investigate-hosts/actions/types';
import { LIFECYCLE } from 'redux-pack';
import makePackAction from '../../../../helpers/make-pack-action';
import { libraries } from '../../../state/state';
import _ from 'lodash';

module('Unit | Reducers | libraries');

const initialState = Immutable.from({
  library: null,
  libraryLoadingStatus: null,
  selectedRowId: null,
  processList: null
});

test('should return the initial state', function(assert) {
  const result = reducer(undefined, {});
  assert.deepEqual(result, initialState);
});


test('The RESET_HOST_DETAILS will reset the state', function(assert) {
  const previous = Immutable.from({
    library: { 1: { path: '/root', fileProperties: { fileName: 'test' } } }
  });
  const result = reducer(previous, { type: ACTION_TYPES.RESET_HOST_DETAILS });

  assert.deepEqual(result, initialState);
});

test('The SET_DLLS_SELECTED_ROW will sets the selected row to state', function(assert) {
  const previous = Immutable.from({
    selectedRowId: null
  });
  const result = reducer(previous, { type: ACTION_TYPES.SET_DLLS_SELECTED_ROW, payload: { id: 123 } });

  assert.equal(result.selectedRowId, 123, 'Expected to match the selected id 123');
});

test('The FETCH_FILE_CONTEXT_DLLS sets normalized server response to state', function(assert) {
  const previous = Immutable.from({
    library: null
  });
  const action = makePackAction(LIFECYCLE.SUCCESS, {
    type: ACTION_TYPES.FETCH_FILE_CONTEXT_DLLS,
    payload: { data: libraries }
  });

  const endState = reducer(previous, action);
  assert.deepEqual(_.values(endState.library).length, 8);
});
