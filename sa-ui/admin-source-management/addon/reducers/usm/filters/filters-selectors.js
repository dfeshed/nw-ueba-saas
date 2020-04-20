import reselect from 'reselect';
import { lookup } from 'ember-dependency-lookup';

const { createSelector } = reselect;

// Contains all the expression saved + newly added expression from the UI
export const savedFilter = (state) => state.filterState.selectedFilter;
const _filterTypes = (state) => state.filterTypes;

export const expressionList = createSelector(
  [savedFilter],
  (savedFilter) => {
    if (savedFilter) {
      return savedFilter.criteria.expressionList;
    }
    return [];
  }
);

/**
 * Filters only searchable columns from list of all available filter control configuration. Also it set's the saved
 * expression to the filter configuration if any. For newly added filter setting showFilterOnInsert flag to `true`.
 * @param searchableColumns
 * @returns {Array}
 * @public
 */
export const filters = createSelector(
  [expressionList, _filterTypes],
  (expressionList, filterTypes) => {
    const i18n = lookup('service:i18n');
    return filterTypes.map((item) => {
      const { label, name, type } = item;
      const expression = expressionList.findBy('propertyName', name); // check if column is searchable
      if (expression) { // Add the config only if it's searchable
        const { propertyValues /* , restrictionType */ } = expression;
        let filterValue = null;
        switch (type) {
          // case 'date':
          //   filterValue = { value: propertyValues.mapBy('value'), operator: restrictionType, unit: propertyValues[0].relativeValueType };
          //   break;
          // case 'text':
          //   filterValue = { value: propertyValues.mapBy('value'), operator: restrictionType };
          //   break;
          case 'list':
          case 'range':
          case 'dropdown':
            filterValue = propertyValues.mapBy('value');
            break;
          // case 'number': {
          //   const values = convertFromBytes(propertyValues.mapBy('value'));
          //   const value = values.mapBy('value');
          //   const [unit] = values.mapBy('unit');
          //   filterValue = { value, unit, operator: restrictionType };
          //   break;
          // }
        }
        return {
          ...item,
          filterValue,
          label: i18n.t(label)
        };
      }
      return { ...item, label: i18n.t(label) };
    });
  }
);
