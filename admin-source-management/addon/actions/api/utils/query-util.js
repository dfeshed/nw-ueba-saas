const addSortBy = (query, key, descending = false) => {
  if (!query.sort) {
    query.sort = {};
  }
  query.sort = { keys: [key], descending };
  return query;
};
/**
 * Adds filter configuration for the query. Filtering the non empty property value expression from the list
 * @param query
 * @param expressionList
 * @returns {*}
 * @public
 */
const addFilter = (query, expressionList) => {
  if (expressionList && expressionList.length) {
    const list = expressionList.filterBy('propertyValues');
    if (list.length) {
      const modifiedList = list.filter((item) => item.propertyValues.length);
      if (modifiedList.length) {
        query.criteria = {
          expressionList: modifiedList,
          predicateType: 'AND'
        };
      }
    }
  }
  return query;
};

export {
  addSortBy,
  addFilter
};
