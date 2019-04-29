import endpoints from './endpoint-location';
import fetch from 'component-lib/services/fetch';
import _ from 'lodash';

const _downloadFile = (bodyBlob, fileName) => {
  const blobURL = window.URL.createObjectURL(bodyBlob);
  const tempLink = document.createElement('a');
  tempLink.style.display = 'none';
  tempLink.href = blobURL;
  tempLink.setAttribute('download', fileName);
  // Safari thinks _blank anchor are pop ups. We only want to set _blank
  // target if the browser does not support the HTML5 download attribute.
  // This allows you to download files in desktop safari if pop up blocking
  // is enabled.
  if (typeof tempLink.download === 'undefined') {
    tempLink.setAttribute('target', '_blank');
  }
  document.body.appendChild(tempLink);
  tempLink.click();
  document.body.removeChild(tempLink);
  setTimeout(() => {
    // For Firefox it is necessary to delay revoking the ObjectURL
    window.URL.revokeObjectURL(blobURL);
  }, 100);
};

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
    if (!fetched.json) {
      return 'error';
    }
    return fetched.json();
  }).catch(() => {
    return 'error';
  });
};

export const exportData = (endpointLocation, data = {}, fileName) => {
  let fetchUrl = endpoints[endpointLocation];
  const options = {
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
    }
  };
  _.forEach(data, (value, key) => {
    if (value !== null) {
      value = (typeof value === 'object') ? value.join(',') : value;
      fetchUrl = fetchUrl.concat(`${key}=${value}&`);
    }
  });
  return fetch(fetchUrl, options).then((fetched) => {
    _downloadFile(fetched._bodyBlob, fileName);
  });
};