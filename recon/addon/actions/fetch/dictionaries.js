import { basicPromiseRequest } from '../util/query-util';

const fetchLanguage = ({ endpointId, eventId }) => {
  return basicPromiseRequest(
    endpointId,
    eventId,
    'core-meta-key',
    { cancelPreviouslyExecuting: true } // can only have one event in recon at a time
  );
};

const fetchAliases = ({ endpointId, eventId }) => {
  return basicPromiseRequest(
    endpointId,
    eventId,
    'core-meta-alias',
    { cancelPreviouslyExecuting: true } // can only have one event in recon at a time
  );
};

export {
  fetchLanguage,
  fetchAliases
};