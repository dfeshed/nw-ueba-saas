import reselect from 'reselect';
import { FILTER_TYPES } from './filter-types';

const { createSelector } = reselect;

// Contains all the expression saved + newly added expression from the UI
const expressionList = (state) => state.endpoint.filter.expressionList || [];
const lastFilterAdded = (state) => state.endpoint.filter.lastFilterAdded;


const schema = (state) => state.endpoint.filter.schemas || [];

export const searchableColumns = createSelector(
  schema,
  (schema) => {
    return schema.filter((item) => item.searchable);
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
  [searchableColumns, expressionList, lastFilterAdded],
  (searchableColumns, expressionList, lastFilterAdded) => {
    // If there are no searchable columns then return empty config
    if (!searchableColumns.length) {
      return [];
    }
    return FILTER_TYPES.map((item) => {
      const { propertyName, isDefault } = item;
      const column = searchableColumns.findBy('name', propertyName); // check if column is searchable
      if (column) { // Add the config only if it's searchable
        const { values = [], dataType } = column;
        let { options = [] } = item;
        if (values) {
          options = [...options, ...values];
        }
        const expression = expressionList.findBy('propertyName', propertyName);
        const selected = !!expression;
        const showRemoveButton = !isDefault && selected;
        const showFilterOnInsert = lastFilterAdded && lastFilterAdded === propertyName;

        return {
          ...item,
          options,
          dataType,
          expression,
          selected,
          showRemoveButton,
          showFilterOnInsert
        };
      }
    }).compact();
  }
);
export const appliedFilters = createSelector(
  filters,
  (filters) => {
    return filters.filter((item) => item.isDefault || item.selected);
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