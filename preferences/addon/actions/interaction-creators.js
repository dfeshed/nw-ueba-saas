import * as ACTION_TYPES from './types';
import { fetchPreferences, savePreferences } from 'preferences/actions/fetchPreferences';

export const togglePreferencesPanel = (additionalFilters, preferencesConfig) => ({
  type: ACTION_TYPES.TOGGLE_PREFERENCES_PANEL,
  payload: { additionalFilters, preferencesConfig }
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
    const { preferencesConfig } = getState().preferences;
    dispatch({
      type: ACTION_TYPES.LOAD_PREFERENCES,
      promise: fetchPreferences(preferencesConfig.modelName, preferencesConfig.additionalFilters)
    });
  };
};

export const saveNewPreferences = (preferencesField, preferenceValue) => {
  return (dispatch, getState) => {
    const { preferencesConfig, preferences, additionalFilters } = getState().preferences;
    let preferencesToSave = preferences.setIn(preferencesField.split('.'), preferenceValue);
    const addtionalFilterKey = preferencesConfig.additionalFilterKey;
    if (addtionalFilterKey) {
      preferencesToSave = preferencesToSave.setIn(addtionalFilterKey.split('.'), additionalFilters);
    }
    dispatch({
      type: ACTION_TYPES.SAVE_PREFERENCES,
      payload: preferencesField,
      promise: savePreferences(preferencesConfig.modelName, preferencesToSave)
    });
  };
};
