import { basicPromiseRequest } from './util/query-util';

const fetchResourceTypes = () => {
  return basicPromiseRequest('findAll', 'live-search-resource-types', '/rsa/cms/search/get-resource-types');
};

const fetchMedia = () => {
  return basicPromiseRequest('findAll', 'live-search-mediums', '/rsa/cms/search/get-resource-mediums');
};

const fetchMetaKeys = () => {
  return basicPromiseRequest('findAll', 'live-search-meta-keys', '/rsa/cms/search/get-resource-meta-keys');
};

const fetchMetaValues = () => {
  return basicPromiseRequest('findAll', 'live-search-meta-values', '/rsa/cms/search/get-resource-meta-values');
};

const fetchCategories = () => {
  return basicPromiseRequest('findAll', 'live-search-categories', '/rsa/cms/search/get-resource-categories');
};

export {
  fetchResourceTypes,
  fetchMedia,
  fetchMetaKeys,
  fetchMetaValues,
  fetchCategories
};