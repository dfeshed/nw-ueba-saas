import { handleActions } from 'redux-actions';
import { handle } from 'redux-pack';
import Immutable from 'seamless-immutable';

import * as ACTION_TYPES from 'recon/actions/types';

const headerInitialState = {
  headerError: null,
  headerErrorCode: null,
  headerItems: null,
  headerLoading: null
};

const headerReducer = handleActions({
  [ACTION_TYPES.INITIALIZE]: () => Immutable.from(headerInitialState),

  [ACTION_TYPES.SUMMARY_RETRIEVE]: (state, action) => {
    return handle(state, action, {
      start: (/* s */) => Immutable.from(headerInitialState).set('headerLoading', true),
      finish: (s) => s.set('headerLoading', false),
      failure: (s) => s.merge({
        headerError: true,
        headerErrorCode: action.payload.code
      }),
      success: (s) => s.set('headerItems', Immutable.from(action.payload.headerItems))
    });
  }

}, Immutable.from(headerInitialState));

export default headerReducer;

