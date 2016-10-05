import { basicPromiseRequest } from './util/query-util';

const fetchLanguage = ({ endpointId, eventId }) => {
  return basicPromiseRequest(endpointId, eventId, 'core-meta-key');
};

const fetchAliases = ({ endpointId, eventId }) => {
  return basicPromiseRequest(endpointId, eventId, 'core-meta-alias');
};

export {
  fetchLanguage,
  fetchAliases
};