import { handleActions } from 'redux-actions';
import * as ACTION_TYPES from 'preferences/actions/types';
import { handle } from 'redux-pack';
import Immutable from 'seamless-immutable';

const resetState = Immutable.from({
  launchFor: null,
  isExpanded: false,
  additionalFilters: null,
  preferences: null,
  isClicked: false
});

const initialState = Immutable.from(resetState);

const preferenceReducer = handleActions({

  [ACTION_TYPES.TOGGLE_PREFERENCES_PANEL]: (state, { payload }) => state.merge({
    ...state,
    launchFor: payload,
    isClicked: true,
    ...payload,
    isExpanded: !state.isExpanded
  }),

  [ACTION_TYPES.CLOSE_PREFERENCES_PANEL]: (state) => state.merge({ ...state, isExpanded: false }),

  [ACTION_TYPES.LOAD_PREFERENCES]: (state, action) => {
    return handle(state, action, {
      start: (state) => state.set('preferences', null),
      failure: (state) => state.merge({ ...state, preferences: action.payload }),
      success: (state) => state.merge({ ...state, preferences: action.payload })
    });
  },

  [ACTION_TYPES.SAVE_PREFERENCES]: (state, action) => {
    return handle(state, action, {
      start: (state) => state.set('preferences', null),
      failure: (state) => state.merge({ ...state }),
      success: (state) => state.merge({ ...state, preferences: action.payload })
    });
  },

  [ACTION_TYPES.UPDATE_PANEL_CLICKED]: (state, { payload }) => state.set('isClicked', payload),

  [ACTION_TYPES.RESET_PREFERENCES_PANEL]: () => (Immutable.from(resetState))

}, initialState);

export default preferenceReducer;
