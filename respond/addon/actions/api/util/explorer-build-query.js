import filterQuery from 'respond/utils/filter-query';
import moment from 'moment';

export default (filters = { created: { name: 'ALL_TIME', unit: 'years', subtract: 50 } }, { sortField, isSortDescending = true }, defaultDateFilterField) => {
  const query = filterQuery.create().addSortBy(sortField, isSortDescending);

  Object.keys(filters).forEach((filterField) => {
    const value = filters[filterField];

    if (filterField === defaultDateFilterField) {
      if ('start' in value) {  // Custom Range Filter
        query.addRangeFilter(defaultDateFilterField, value.start || 0, value.end || undefined);
      } else { // Common date/time range filter
        query.addRangeFilter(defaultDateFilterField, moment().subtract(value.subtract, value.unit).valueOf(), undefined);
      }
    } else {
      query.addFilter(filterField, filters[filterField]);
    }
  });

  return query;
};