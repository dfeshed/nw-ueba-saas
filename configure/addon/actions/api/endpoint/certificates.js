import { lookup } from 'ember-dependency-lookup';
import {
  addSortBy,
  addFilter
} from 'configure/actions/utils/query-util';

const getCertificates = (pageNumber, sort, expressionList) => {
  let query = {
    pageNumber: pageNumber || 0,
    pageSize: 100
  };
  const request = lookup('service:request');
  const { sortField, isSortDescending: isDescending } = sort;
  query = addSortBy(query, sortField, isDescending);
  query = addFilter(query, expressionList);
  return request.promiseRequest({
    method: 'getCertificates',
    modelName: 'endpoint-certificates',
    query: {
      data: query
    }
  });
};


const setCertificateStatus = (data) => {
  const request = lookup('service:request');
  return request.promiseRequest({
    method: 'setCertificateStatus',
    modelName: 'context-data',
    query: {
      data
    }
  });
};


const getCertificateStatus = (selections) => {
  const request = lookup('service:request');
  const data = {
    'filter': [
      { 'field': 'dataSourceType',
        'value': 'CertificateStatus'
      },
      {
        'field': 'id',
        'values': selections
      }
    ]
  };
  return request.promiseRequest({
    method: 'getCertificateStatus',
    modelName: 'context-data',
    streamOptions: { requireRequestId: true },
    query: data
  });
};


export default {
  getCertificates,
  getCertificateStatus,
  setCertificateStatus
};
