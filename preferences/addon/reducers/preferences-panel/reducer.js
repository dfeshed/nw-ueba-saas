import { handleActions } from 'redux-actions';
import * as ACTION_TYPES from 'preferences/actions/types';

const initialState = {
  launchFor: null,
  expanded: false
};

export default handleActions({

  [ACTION_TYPES.TOGGLE_PREFERENCES_PANEL]: (state, { payload }) => ({
    ...state,
    launchFor: payload,
    expanded: !state.expanded
  }),

  [ACTION_TYPES.CLOSE_PREFERENCES_PANEL]: (state) => ({ ...state, expanded: false }),

  [ACTION_TYPES.RESET_PREFERENCES_PANEL]: () => ({ ...initialState })

}, initialState);