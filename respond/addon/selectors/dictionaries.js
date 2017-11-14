import reselect from 'reselect';
import _ from 'lodash';

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

export const getTopLevelCategoryNames = createSelector(
  getCategoryTags,
  (categories) => categories.mapBy('parent').uniq().compact()
);

export const getGroupedCategories = createSelector(
  getCategoryTags,
  (categories) => {
    const groupedCategories = categories.reduce((groups, item) => {
      if (!groups[item.parent]) {
        groups[item.parent] = { groupName: item.parent, options: [item] };
      } else {
        groups[item.parent].options.push(item);
      }
      return groups;
    }, {});
    return _.values(groupedCategories);
  }
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
