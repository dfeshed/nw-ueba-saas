import reselect from 'reselect';
import _ from 'lodash';

const { createSelector } = reselect;

const dictionariesState = (state) => state.configure.respond.dictionaries;

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
