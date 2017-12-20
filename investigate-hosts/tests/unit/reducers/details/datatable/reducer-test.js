import Immutable from 'seamless-immutable';
import { test, module } from 'qunit';
import reducer from 'investigate-hosts/reducers/details/datatable/reducer';
import * as ACTION_TYPES from 'investigate-hosts/actions/types';

module('Unit | Reducers | details-datatable');

const initialState = Immutable.from({
  sortConfig: {
    autoruns: null,
    services: null,
    tasks: null,
    libraries: null,
    drivers: null
  }
});

test('should return the initial state', function(assert) {
  const result = reducer(undefined, {});
  assert.deepEqual(result, initialState);
});


test('The HOST_DETAILS_DATATABLE_SORT_CONFIG will change the state', function(assert) {
  const result = reducer(initialState, {
    type: ACTION_TYPES.HOST_DETAILS_DATATABLE_SORT_CONFIG,
    payload: { tabName: 'SERVICES', isDescending: false, field: 'fieldName' }
  });

  assert.deepEqual(result.sortConfig.services, { isDescending: false, field: 'fieldName' });
});

