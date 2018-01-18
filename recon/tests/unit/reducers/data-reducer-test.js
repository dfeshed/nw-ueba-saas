import { test, module } from 'qunit';
import reducer from 'recon/reducers/data-reducer';
import * as ACTION_TYPES from 'recon/actions/types';
import Immutable from 'seamless-immutable';

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
