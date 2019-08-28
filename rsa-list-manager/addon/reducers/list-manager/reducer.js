import Immutable from 'seamless-immutable';
import { handleActions } from 'redux-actions';
import { handle } from 'redux-pack';

import * as ACTION_TYPES from 'rsa-list-manager/actions/types';

const _initialState = Immutable.from({
  foo: null
});

export default handleActions({
  [ACTION_TYPES.SOME_ACTION_1]: (state, action) => {
    return handle(state, action, {
      success: (s) => {
        return s;
      }
    });
  }
}, _initialState);
