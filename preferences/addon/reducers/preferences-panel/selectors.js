import reselect from 'reselect';
import defaultConfig from 'preferences/config/index';

const { createSelector } = reselect;

// ACCESSOR FUNCTIONS
const _preferences = (state) => state.preferences;
const _launchFor = (state) => state.launchFor;

export const getDbEndTime = (state) => state.investigate.services.summaryData.endTime;
export const getDbStartTime = (state) => state.investigate.services.summaryData.startTime;
// SELECTOR FUNCTIONS
export const getPreferencesConfig = createSelector(
  [_preferences, _launchFor],
  (preferences, launchFor) => {
    if (defaultConfig[launchFor] && preferences) {
      return defaultConfig[launchFor].items;
    }
    return null;
  }
);

export const getContextualHelp = createSelector(
  [_launchFor],
  (launchFor) => {
    if (defaultConfig[launchFor]) {
      return defaultConfig[launchFor].helpIds;
    }
  }
);
