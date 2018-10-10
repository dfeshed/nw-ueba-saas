import endpoints from './endpoint-location';
import fetch from 'component-lib/services/fetch';
import _ from 'lodash';

export const fetchData = (endpointLocation, filter = {}) => {
  let fetchUrl = endpoints[endpointLocation];
  _.forEach(filter, (value, key) => {
    if (value !== null) {
      value = (typeof value === 'object') ? value.join(',') : value;
      fetchUrl = fetchUrl.concat(`${key}=${value}&`);
    }
  });
  return fetch(fetchUrl).then((fetched) => fetched.json());
};