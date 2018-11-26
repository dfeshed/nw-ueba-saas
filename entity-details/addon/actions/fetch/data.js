import endpoints from './endpoint-location';
import fetch from 'component-lib/services/fetch';
import _ from 'lodash';

export const fetchData = (endpointLocation, data = {}, method, args) => {
  let fetchUrl = endpoints[endpointLocation];
  fetchUrl = args ? fetchUrl.replace(/{(.*)}/, args) : fetchUrl;
  let options = null;
  if (method) {
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