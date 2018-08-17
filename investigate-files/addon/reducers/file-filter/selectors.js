import { createSelector } from 'reselect';
import { lookup } from 'ember-dependency-lookup';

import { FILTER_TYPES } from './filter-type';


// Contains all the expression saved + newly added expression from the UI
const savedFilter = (state) => state.files.filter.selectedFilter;

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
  const values = bytes.map((item) => {
    const { value } = item;
    if (item.value >= GB) {
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
  savedFilter,
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
  [expressionList],
  (expressionList) => {
    const i18n = lookup('service:i18n');
    return FILTER_TYPES.map((item) => {
      const { label, name, type } = item;
      const expression = expressionList.findBy('propertyName', name); // check if column is searchable
      if (expression) { // Add the config only if it's searchable
        const { propertyValues, restrictionType } = expression;
        let filterValue = null;
        switch (type) {
          case 'text':
            filterValue = { value: propertyValues.mapBy('value'), operator: restrictionType };
            break;
          case 'list':
          case 'range':
          case 'dropdown':
            filterValue = propertyValues.mapBy('value');
            break;
          case 'number': {
            const values = convertFromBytes(propertyValues.mapBy('value'));
            const value = values.mapBy('value');
            const unit = values.mapBy('unit');
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

/**
 * Returns list of all the filters which are not defaults. This will be displayed in more filter dropdown
 * @public
 */
export const listWithoutDefault = createSelector(
  filters,
  (filters) => {
    return filters.filter((item) => !item.isDefault);
  }
);
