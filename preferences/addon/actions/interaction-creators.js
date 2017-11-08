import * as ACTION_TYPES from './types';
import { fetchPreferences, savePreferences } from 'preferences/actions/fetchPreferences';
import defaultConfig from 'preferences/config/index';

export const togglePreferencesPanel = (launchFor, additionalFilters) => ({
  type: ACTION_TYPES.TOGGLE_PREFERENCES_PANEL,
  payload: { launchFor, additionalFilters }
});

export const updatePanelClicked = (state) => ({
  type: ACTION_TYPES.UPDATE_PANEL_CLICKED,
  payload: state
});

export const closePreferencesPanel = () => ({
  type: ACTION_TYPES.CLOSE_PREFERENCES_PANEL
});

export const resetPreferencesPanel = () => ({
  type: ACTION_TYPES.RESET_PREFERENCES_PANEL
});

export const loadPreferences = () => {
  return (dispatch, getState) => {
    const { launchFor, additionalFilters } = getState().preferences;
    dispatch({
      type: ACTION_TYPES.LOAD_PREFERENCES,
      promise: fetchPreferences(launchFor, additionalFilters)
    });
  };
};

export const saveNewPreferences = (preferencesField, preferenceValue) => {
  return (dispatch, getState) => {
    const { launchFor, preferences, additionalFilters } = getState().preferences;
    let preferencesToSave = preferences.setIn(preferencesField.split('.'), preferenceValue);
    const addtionalFilterKey = defaultConfig[launchFor].additionalFilterKey;
    if (addtionalFilterKey) {
      preferencesToSave = preferencesToSave.setIn(addtionalFilterKey.split('.'), additionalFilters);
    }
    dispatch({
      type: ACTION_TYPES.SAVE_PREFERENCES,
      promise: savePreferences(launchFor, preferencesToSave)
    });
  };
};
