import filterQuery from 'respond/utils/filter-query';
import moment from 'moment';

export default (filters = { created: { name: 'ALL_TIME', unit: 'years', subtract: 50 } }, { sortField, isSortDescending = true }, defaultDateFilterField) => {
  const query = filterQuery.create().addSortBy(sortField, isSortDescending);

  Object.keys(filters).forEach((filterField) => {
    const value = filters[filterField];
    // isNull filters
    if (value && value.isNull !== undefined) {
      query.appendFilter(value);
    } else if (value && value.isRange === true) { // Non-date range filters
      query.addRangeFilter(filterField, value.start || 0, value.end || undefined, value.type || 'date');
    } else if (filterField === defaultDateFilterField) { // Date range filters
      if ('start' in value) {  // Custom Date Range Filter
        query.addRangeFilter(defaultDateFilterField, value.start || 0, value.end || undefined);
      } else { // Common date/time range filter
        query.addRangeFilter(defaultDateFilterField, moment().subtract(value.subtract, value.unit).valueOf(), undefined);
      }
    } else {
      query.addFilter(filterField, value);
    }
  });

  return query;
};