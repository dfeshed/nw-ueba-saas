import * as ACTION_TYPES from './types';
import { lookup } from 'ember-dependency-lookup';

const prefService = lookup('service:preferences');

export const togglePreferencesPanel = (launchFor) => ({
  type: ACTION_TYPES.TOGGLE_PREFERENCES_PANEL,
  payload: launchFor
});

export const closePreferencesPanel = () => ({
  type: ACTION_TYPES.CLOSE_PREFERENCES_PANEL
});

export const resetPreferencesPanel = () => ({
  type: ACTION_TYPES.RESET_PREFERENCES_PANEL
});

export const loadPreferences = () => {
  return (dispatch, getState) => {
    const state = getState();
    prefService.getPreferences(state.preferences.launchFor).then(({ data }) => {
      dispatch({
        type: ACTION_TYPES.LOAD_PREFERENCES,
        payload: data
      });
    }).catch(() => {
      dispatch({
        type: ACTION_TYPES.LOAD_PREFERENCES_ERROR,
        payload: null
      });
    });
  };
};

export const savePreferences = (preferencesToSave) => {
  return (dispatch, getState) => {
    const { launchFor } = getState().preferences;
    dispatch({
      type: ACTION_TYPES.SAVE_PREFERENCES_INIT
    });
    prefService.setPreferences(launchFor, preferencesToSave).then(() => {
      dispatch({
        type: ACTION_TYPES.SAVE_PREFERENCES
      });
    });
  };
};