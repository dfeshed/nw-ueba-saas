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

export default {
  getCertificates
};
