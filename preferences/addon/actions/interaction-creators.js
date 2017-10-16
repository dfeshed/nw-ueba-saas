import * as ACTION_TYPES from './types';

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