import { basicPromiseRequest } from './util/query-util';
import CmsSearchRequest from 'sa/utils/cms-search';

const fetchResults = (criteria) => {
  const cmsSearchRequest = CmsSearchRequest.create();
  const { pageSize, pageNumber, sort, ...searchCriteria } = criteria;

  cmsSearchRequest.setSearchCriteria(searchCriteria);
  cmsSearchRequest.setPageNumber(pageNumber);
  cmsSearchRequest.setPageSize(pageSize);
  cmsSearchRequest.setSort(sort);

  return basicPromiseRequest('query', 'live-search', '/rsa/cms/search/search', 'POST', cmsSearchRequest.toJSON());
};

export {
  fetchResults
};