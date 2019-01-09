import { isBlank } from '@ember/utils';

const HASH_COLUMNS = ['checksumSha256', 'checksumMd5', 'checksumSha1'];
const ENDPOINT_IP_MAPPING = 'machineIdentity.networkInterfaces.ipv4';
const ENDPOINT_MAC_NAME_MAPPING = 'machineIdentity.networkInterfaces.macAddress';
const ENDPOINT_HOST_MAPPING = 'machineIdentity.machineName';

const INVESTIGATE_ENDPOINT_META_MAPPING = {
  'alias.host': ENDPOINT_HOST_MAPPING,
  'device.host': ENDPOINT_HOST_MAPPING,
  'host.src': ENDPOINT_HOST_MAPPING,
  'host.dst': ENDPOINT_HOST_MAPPING,
  'ip.src': ENDPOINT_IP_MAPPING,
  'ip.dst': ENDPOINT_IP_MAPPING,
  'device.ip': ENDPOINT_IP_MAPPING,
  'paddr': ENDPOINT_IP_MAPPING,
  'ip.addr': ENDPOINT_IP_MAPPING,
  'alias.ip': ENDPOINT_IP_MAPPING,
  'alias.mac': ENDPOINT_MAC_NAME_MAPPING,
  'eth.src': ENDPOINT_MAC_NAME_MAPPING,
  'eth.dst': ENDPOINT_MAC_NAME_MAPPING
};

const MONGO_OPERATOR_MAPPING = {
  '=': 'EQUAL',
  'contains': 'LIKE'
};

/**
 * Parse the given query string into meta, value and Operator.
 * Assumes the queryString is of the following syntax: `key1 operator1 value1 && key2 operator1 value2
 * @param queryString
 * @private
 */
const parseQueryString = (queryString) => {
  if (isBlank(queryString)) {
    // When uri is empty, return empty array. Alas, ''.split() returns a non-empty array; it's a 1-item array with
    // an empty string in it, which is not what we want.  So we check for '' and return [] explicitly here.
    return [];
  }
  return decodeURIComponent(queryString).split('&&') // currently endpoint is supporting only &&
    .map((queryString) => {
      const [ meta, operator, ...valuePieces ] = queryString.split(' ');
      const value = valuePieces.join(' ').replace(/^'(.*)'$/, '$1'); // Replace the start and end single quotes
      const propertyName = INVESTIGATE_ENDPOINT_META_MAPPING[meta];
      const restrictionType = MONGO_OPERATOR_MAPPING[operator];
      return { propertyName, propertyValues: [ { value } ], restrictionType };
    });
};

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
 * For risk score, when user select min value as 0 we have to include 0 to max as well as null
 * in the filter critieria since in UI we are displayed null risk score as 0
 * @param expressions
 * @returns {*}
 * @private
 */
const _modifiedRiskScoreExpression = (expressions) => {
  const [expression] = expressions.filterBy('propertyName', 'score');
  const expressionList = [];

  if (expression) {
    expressionList.push({
      restrictionType: 'BETWEEN',
      propertyValues: expression.propertyValues,
      propertyName: expression.propertyName
    });
    if (expression.propertyValues[0].value === 0) {
      expressionList.push({
        restrictionType: 'IS_NULL',
        propertyName: expression.propertyName
      });
    }
    return expressionList;
  } else {
    return [];
  }
};

/**
 * For download status, possible options are Error, Downloaded and NotDownloaded
 * For NotDownloaded, filter by NotDownloaded and downloadInfo.status field not present
 * @param expressions
 * @returns {*}
 * @private
 */
const _modifiedDownloadStatusExpression = ((expressions) => {
  const [expression] = expressions.filterBy('propertyName', 'downloadInfo.status');
  const expressionList = [];
  if (expression) {
    expressionList.push({
      restrictionType: 'IN',
      propertyValues: expression.propertyValues,
      propertyName: expression.propertyName
    });
    if (expression.propertyValues.filter((propertyValue) => propertyValue.value === 'NotDownloaded').length) {
      expressionList.push({
        restrictionType: 'IS_NULL',
        propertyName: expression.propertyName
      });
    }
  }
  return expressionList;
});

/**
 * Prepares the criteria list for the filter. If file hash is present then split the hash and if risk score is present then modify
 * existing expression such that null risk score is already included in risk score filter
 * After that add file hash/risk score as a another
 * expression list and remove it from the original list
 * @param list
 * @returns {Array}
 * @private
 */
const _getCriteriaList = (list, type) => {
  const result = [];
  const scores = _modifiedRiskScoreExpression(list);
  const fileDownloadStatus = _modifiedDownloadStatusExpression(list);
  const newList = list.filter((property) => {
    return property.propertyName !== 'fileHash' && property.propertyName !== 'score' && property.propertyName !== 'downloadInfo.status';
  });
  if (newList.length) {
    result.push({
      expressionList: newList,
      predicateType: 'AND'
    });
  }

  // calculate filehash related expression only if type is specified as 'file'
  if (type === 'file') {
    const hashes = _splitHash(list);

    if (hashes.length) {
      result.push({
        expressionList: hashes,
        predicateType: 'OR'
      });
    }
  }

  if (scores.length) {
    result.push({
      expressionList: scores,
      predicateType: 'OR'
    });
  }
  if (fileDownloadStatus.length) {
    result.push({
      expressionList: fileDownloadStatus,
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
const addFilter = (query, expressionList, type = 'file') => {
  if (expressionList && expressionList.length) {
    const list = expressionList.filterBy('propertyValues');
    if (list.length) {
      const modifiedList = list.filter((item) => item.propertyValues.length);

      if (modifiedList.length) {
        query.criteria = {
          criteriaList: _getCriteriaList(modifiedList, type),
          predicateType: 'AND'
        };
      }
    }
  }
  return query;
};

export {
  addSortBy,
  addFilter,
  parseQueryString
};
