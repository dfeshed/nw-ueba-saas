import { basicPromiseRequest } from './util/query-util';

const fetchReconFiles = ({ endpointId, eventId }) => {
  return basicPromiseRequest(endpointId, eventId, 'reconstruction-file-data');
};

export default fetchReconFiles;
