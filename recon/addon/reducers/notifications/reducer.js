import { handleActions } from 'redux-actions';

import * as ACTION_TYPES from 'recon/actions/types';

const notificationsInitialState = {
  // callback for stopping notifications
  // (obtained at run-time as a result from notifications initialization)
  stopNotifications: null
};

const notificationsReducer = handleActions({
  [ACTION_TYPES.INITIALIZE]: (state) => ({
    stopNotifications: state.stopNotifications || null
  }),

  [ACTION_TYPES.NOTIFICATION_INIT_SUCCESS]: (state, { payload }) => ({
    ...state,
    stopNotifications: payload.cancelFn
  }),

  [ACTION_TYPES.NOTIFICATION_TEARDOWN_SUCCESS]: (state) => ({
    // clear the callback that tears down notifications
    ...state,
    stopNotifications: null
  })
}, notificationsInitialState);

export default notificationsReducer;
