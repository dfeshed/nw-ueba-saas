import reselect from 'reselect';

const { createSelector } = reselect;

// ACCESSOR FUNCTIONS
const _preferencesConfiguration = (state) => state.preferencesConfig;

export const getDbEndTime = (state) => state.investigate.services.summaryData.endTime;
export const getDbStartTime = (state) => state.investigate.services.summaryData.startTime;

// SELECTOR FUNCTIONS
export const getPreferencesSchema = createSelector(
  [_preferencesConfiguration],
  (preferencesConfiguration) => {
    if (preferencesConfiguration) {
      return preferencesConfiguration.items;
    }
    return null;
  }
);

export const getContextualHelp = createSelector(
  _preferencesConfiguration,
  (preferencesConfiguration) => {
    if (preferencesConfiguration) {
      return preferencesConfiguration.helpIds;
    }
  }
);
