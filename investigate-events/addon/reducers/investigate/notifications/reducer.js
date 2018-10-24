// *******
// BEGIN - Copy/pasted download code from Recon
// *******
import { handleActions } from 'redux-actions';
import Immutable from 'seamless-immutable';

import * as ACTION_TYPES from 'investigate-events/actions/types';

const notificationsInitialState = Immutable.from({
  // callback for stopping notifications
  // (obtained at run-time as a result from notifications initialization)
  stopNotifications: null
});

export default handleActions({
  [ACTION_TYPES.INITIALIZE_INVESTIGATE]: (state) => {
    return state.set('stopNotifications', state.stopNotifications || null);
  },

  [ACTION_TYPES.NOTIFICATION_INIT_SUCCESS]: (state, { payload }) => {
    return state.set('stopNotifications', payload.cancelFn);
  },

  // clear the callback that tears down notifications
  [ACTION_TYPES.NOTIFICATION_TEARDOWN_SUCCESS]: (state) => {
    return state.set('stopNotifications', null);
  }
}, notificationsInitialState);
// *******
// END - Copy/pasted download code from Recon
// *******