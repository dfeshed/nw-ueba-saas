import reselect from 'reselect';
import {
  getCategoryTags
} from 'respond-shared/selectors/create-incident/selectors';

const { createSelector } = reselect;

const dictionariesState = (state) => state.respond.dictionaries;

export const getStatusTypes = createSelector(
  dictionariesState,
  (dictionariesState) => dictionariesState.statusTypes
);

export const getRemediationStatusTypes = createSelector(
  dictionariesState,
  (dictionariesState) => dictionariesState.remediationStatusTypes
);

export const getTopLevelCategoryNames = createSelector(
  getCategoryTags,
  (categories) => categories.mapBy('parent').uniq().compact()
);

export const getAlertTypes = createSelector(
  dictionariesState,
  (dictionariesState) => dictionariesState.alertTypes
);

export const getAlertSources = createSelector(
  dictionariesState,
  (dictionariesState) => dictionariesState.alertSources
);

export const getAlertNames = createSelector(
  dictionariesState,
  (dictionariesState) => dictionariesState.alertNames
);

export const getMilestoneTypes = createSelector(
  dictionariesState,
  (dictionariesState) => dictionariesState.milestoneTypes
);