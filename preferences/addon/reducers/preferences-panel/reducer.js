import { handleActions } from 'redux-actions';
import * as ACTION_TYPES from 'preferences/actions/types';
import { handle } from 'redux-pack';
import Immutable from 'seamless-immutable';

const resetState = Immutable.from({
  isExpanded: false,
  additionalFilters: null,
  preferences: null,
  isClicked: false,
  shouldPublishPreferences: false,
  changedField: null,
  preferencesConfig: {}
});

const _handleLoadPreferences = (action) => {
  return (state) => {
    const { payload } = action;
    const preferences = payload ? payload : state.preferencesConfig.defaultPreferences;
    return state.merge({
      ...state,
      preferences
    });
  };
};

const initialState = Immutable.from(resetState);

const preferenceReducer = handleActions({

  [ACTION_TYPES.TOGGLE_PREFERENCES_PANEL]: (state, { payload }) => state.merge({
    ...state,
    preferencesConfig: { ...payload },
    ...payload,
    isExpanded: !state.isExpanded
  }),

  [ACTION_TYPES.CLOSE_PREFERENCES_PANEL]: (state) => state.merge({ ...state, isExpanded: false }),

  [ACTION_TYPES.LOAD_PREFERENCES]: (state, action) => {
    return handle(state, action, {
      start: (state) => state.set('preferences', null),
      success: _handleLoadPreferences(action)
    });
  },

  [ACTION_TYPES.SAVE_PREFERENCES]: (state, action) => {
    return handle(state, action, {
      start: (state) => state.set('changedField', action.payload), // record the changed field so that only that preference can be published
      failure: (state) => state.merge({ ...state }),
      success: (state) => state.merge({ ...state, preferences: action.payload, shouldPublishPreferences: true })
    });
  },

  [ACTION_TYPES.UPDATE_PANEL_CLICKED]: (state, { payload }) => state.set('isClicked', payload),

  [ACTION_TYPES.RESET_PREFERENCES_PANEL]: () => (Immutable.from(resetState))

}, initialState);

export default preferenceReducer;
