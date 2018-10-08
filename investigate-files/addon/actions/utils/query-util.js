const HASH_COLUMNS = ['checksumSha256', 'checksumMd5', 'checksumSha1'];

const addSortBy = (query, key, descending = false) => {
  if (!query.sort) {
    query.sort = {};
  }
  query.sort = { keys: [key], descending };
  return query;
};


const splitHash = (expressions) => {
  const [expression] = expressions.filterBy('propertyName', 'fileHash');
  if (expression) {
    const expressionList = HASH_COLUMNS.map((column) => {
      return {
        restrictionType: 'IN',
        propertyValues: expression.propertyValues,
        propertyName: column
      };
    });
    return expressionList;
  } else {
    return [];
  }
};

const getCriteriaList = (list) => {
  const result = [];
  const hashes = splitHash(list);
  const newList = list.rejectBy('propertyName', 'fileHash');
  if (newList.length) {
    result.push({
      expressionList: newList,
      predicateType: 'AND'
    });
  }

  if (hashes.length) {
    result.push({
      expressionList: hashes,
      predicateType: 'OR'
    });
  }

  return result;
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
          criteriaList: getCriteriaList(modifiedList),
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
