import reselect from 'reselect';

const { createSelector } = reselect;

const dictionariesState = (state) => state.respond.dictionaries;

export const getPriorityTypes = createSelector(
  dictionariesState,
  (dictionariesState) => dictionariesState.priorityTypes
);

export const getStatusTypes = createSelector(
  dictionariesState,
  (dictionariesState) => dictionariesState.statusTypes
);

export const getRemediationStatusTypes = createSelector(
  dictionariesState,
  (dictionariesState) => dictionariesState.remediationStatusTypes
);

export const getRemediationTypes = createSelector(
  dictionariesState,
  (dictionariesState) => dictionariesState.remediationTypes
);

export const getCategoryTags = createSelector(
  dictionariesState,
  (dictionariesState) => dictionariesState.categoryTags
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
