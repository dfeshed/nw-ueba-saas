import { basicPromiseRequest } from '../util/query-util';

const fetchReconFiles = ({ endpointId, eventId }) => {
  return basicPromiseRequest(
    endpointId,
    eventId,
    'reconstruction-file-data',
    { cancelPreviouslyExecuting: true } // can only have one event in recon at a time
  );
};

export default fetchReconFiles;
