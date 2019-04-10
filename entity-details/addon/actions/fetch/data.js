import endpoints from './endpoint-location';
import fetch from 'component-lib/services/fetch';
import _ from 'lodash';

/**
 * This fetch data consumes config object and return data based on REST endpoint response.
 * Ex.
 * {
 *   restEndpointLocation: '/presidio/users'
 *   method: 'GET/POST',
 *   urlParameters: { key: value },
 *   data: {} // data to POST or query param
 * }
 *
 * @private
*/

export const fetchData = ({ restEndpointLocation, data = {}, method, urlParameters }) => {
  let fetchUrl = endpoints[restEndpointLocation];
  fetchUrl = urlParameters ? fetchUrl.replace(/{(.*)}/, urlParameters) : fetchUrl;
  let options = null;
  if (method && method !== 'GET') {
    options = {
      method,
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(data)
    };
  } else {
    options = {
      headers: {
        'Content-Type': 'application/json'
      }
    };
    _.forEach(data, (value, key) => {
      if (value !== null) {
        value = (typeof value === 'object') ? value.join(',') : value;
        fetchUrl = fetchUrl.concat(`${key}=${value}&`);
      }
    });
  }
  return fetch(fetchUrl, options).then((fetched) => {
    return fetched.json();
  });
};