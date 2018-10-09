const HASH_COLUMNS = ['checksumSha256', 'checksumMd5', 'checksumSha1'];

const addSortBy = (query, key, descending = false) => {
  if (!query.sort) {
    query.sort = {};
  }
  query.sort = { keys: [key], descending };
  return query;
};

/**
 * For file hash user can input sha256 or sha1 or md5, so we need to query given value for all the three columns
 * so splinting the hash into multiple colums
 * @param expressions
 * @returns {*}
 * @private
 */
const _splitHash = (expressions) => {
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

/**
 * Prepares the criteria list for the filter. If file hash is present then split the hash and add it as a another
 * expression list and remove it from the original list
 * @param list
 * @returns {Array}
 * @private
 */
const _getCriteriaList = (list) => {
  const result = [];
  const hashes = _splitHash(list);
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
          criteriaList: _getCriteriaList(modifiedList),
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
