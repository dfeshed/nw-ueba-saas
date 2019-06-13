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
      start: (s) => {
        return s.merge({
          headerLoading: true
        });
      },
      always: (s) => {
        // in the case of missing perms, MT will not respond to this request
        // if success or failure, headerLoading will be false before arriving here
        if (s.headerLoading) {
          return s.merge({
            headerLoading: false,
            headerError: true
          });
        } else {
          return s;
        }
      },
      finish: (s) => s.set('headerLoading', false),
      failure: (s) => {
        return s.merge({
          headerLoading: false,
          headerError: true,
          headerErrorCode: action.payload.code
        });
      },
      success: (s) => {
        return s.merge({
          headerLoading: false,
          headerError: false,
          headerItems: Immutable.from(action.payload.headerItems)
        });
      }
    });
  }

}, Immutable.from(headerInitialState));

export default headerReducer;
