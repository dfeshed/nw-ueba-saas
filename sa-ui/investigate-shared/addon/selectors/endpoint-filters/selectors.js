import { createSelector } from 'reselect';
import { lookup } from 'ember-dependency-lookup';

// Contains all the expression saved + newly added expression from the UI
export const savedFilter = (state) => state.filter.selectedFilter;
const _filterTypes = (state) => state.filterTypes;

/**
 * Converts entered value and unit to bytes
 * @param bytes
 * @public
 */
const convertFromBytes = (bytes) => {
  let val = null;
  let unit = null;
  const GB = Math.pow(1024, 3);
  const MB = Math.pow(1024, 2);
  const KB = 1024;
  const values = bytes.map((value) => {
    if (value >= GB) {
      val = (value / GB).toFixed(1);
      unit = 'GB';
    } else if (value >= MB) {
      val = (value / MB).toFixed(1);
      unit = 'MB';
    } else if (value >= KB) {
      val = (value / KB).toFixed(1);
      unit = 'KB';
    } else {
      val = value;
      unit = 'bytes';
    }
    return { value: val.toString(), unit };
  });
  return values;
};

export const expressionList = createSelector(
  [savedFilter],
  (savedFilter) => {
    if (savedFilter) {
      return savedFilter.criteria.expressionList;
    }
    return [];
  }
);

export const isSystemFilter = createSelector(
  savedFilter,
  (savedFilter) => {
    return savedFilter && savedFilter.systemFilter;
  }
);

export const selectedFilterId = createSelector(
  savedFilter,
  (savedFilter) => {
    return savedFilter && savedFilter.id;
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
        const { propertyValues, restrictionType } = expression;
        let filterValue = null;
        switch (type) {
          case 'date':
            filterValue = { value: propertyValues.mapBy('value'), operator: restrictionType, unit: propertyValues[0].relativeValueType };
            break;
          case 'text':
            filterValue = { value: propertyValues.mapBy('value'), operator: restrictionType };
            break;
          case 'list':
          case 'range':
          case 'dropdown':
            filterValue = propertyValues.mapBy('value');
            break;
          case 'number': {
            let unit, value;
            if (expression.propertyName === 'size') {
              const values = convertFromBytes(propertyValues.mapBy('value'));
              value = values.mapBy('value');
              [unit] = values.mapBy('unit');
            } else {
              value = propertyValues.mapBy('value');
            }
            filterValue = { value, unit, operator: restrictionType };
            break;
          }
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
