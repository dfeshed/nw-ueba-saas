import { handleActions } from 'redux-actions';
import * as ACTION_TYPES from 'preferences/actions/types';

const initialState = {
  launchFor: null,
  expanded: false,
  data: null,
  status: null,
  clicked: false
};

export default handleActions({

  [ACTION_TYPES.TOGGLE_PREFERENCES_PANEL]: (state, { payload }) => ({
    ...state,
    launchFor: payload,
    clicked: true,
    expanded: !state.expanded
  }),

  [ACTION_TYPES.CLOSE_PREFERENCES_PANEL]: (state) => ({ ...state, expanded: false, clicked: false }),

  [ACTION_TYPES.LOAD_PREFERENCES]: (state, { payload }) => ({ ...state, status: 'success', data: payload }),

  [ACTION_TYPES.LOAD_PREFERENCES_ERROR]: (state, { payload }) => ({ ...state, status: 'error', data: payload }),

  [ACTION_TYPES.SAVE_PREFERENCES]: (state) => ({ ...state, status: 'success' }),

  [ACTION_TYPES.SAVE_PREFERENCES_INIT]: (state) => ({ ...state, status: null }),

  [ACTION_TYPES.UPDATE_PANEL_STATE]: (state, { payload }) => ({ ...state, clicked: payload }),

  [ACTION_TYPES.RESET_PREFERENCES_PANEL]: () => ({ ...initialState })

}, initialState);
