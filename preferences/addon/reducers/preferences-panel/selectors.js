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
    if (preferencesConfiguration && preferencesConfiguration.items) {
      const { fieldPrefix } = preferencesConfiguration;

      // TODO bring download back. Remove filter that excludes Meta Download option.
      const itemList = preferencesConfiguration.items.filter((item) => item.name != 'preferences.investigate-events.defaultMetaFormat');

      const items = itemList.map((item) => {
        return {
          ...item,
          fieldPrefix: item.additionalFieldPrefix ? `${fieldPrefix}.${item.additionalFieldPrefix}` : fieldPrefix
        };
      });
      return items;
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
