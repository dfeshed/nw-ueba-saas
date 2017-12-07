import { isBlank } from 'ember-utils';

const ENDPOINT_IP_MAPPING = 'machine.networkInterfaces.ipv4';
const ENDPOINT_MAC_NAME_MAPPING = 'machine.networkInterfaces.macAddress';
const ENDPOINT_HOST_MAPPING = 'machine.machineName';

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
  '=': 'EQUAL'
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
      const value = valuePieces.join(' ').replace(/^"(.*)"$/, '$1'); // Replace the start and end double quotes
      const propertyName = INVESTIGATE_ENDPOINT_META_MAPPING[meta];
      const restrictionType = MONGO_OPERATOR_MAPPING[operator];
      return { propertyName, propertyValues: [ { value } ], restrictionType };
    });
};


const addSortBy = (query, key, descending = false) => {
  if (!query.sort) {
    query.sort = [];
  }
  query.sort.push({ key, descending });
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
      const modifiedList = list.map((item) => {
        if (item.propertyValues.length) {
          return item;
        }
      }).compact();

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
  addFilter,
  parseQueryString
};
