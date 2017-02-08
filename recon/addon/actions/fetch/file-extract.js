import { promiseRequest } from 'streaming-data/services/data-access/requests';
import { endpointFilter, addFileTypeFilter, addSessionIdsFilter, addFileSelectionsFilter, addFilenameFilter } from './util/query-util';

/**
 * Retrieves an ID from server for a job to extract the files of a given event id.
 * Note: For now, always requests all the event's files (whether selected or not).
 * @returns Promise that will resolve with the server response. The response will NOT include the contents of the
 * requested file(s). Instead, the response (if successful) include a "job" id.  This job can then be monitored with
 * other server calls. If & when the job is completed, the job will yield a download URL, which can be used to fetch
 * the actual files (zipped).
 * @public
 */
function fetchFileExtractJobId(endpointId, eventId, filenames, filename) {

  let query = endpointFilter(endpointId);
  query = addFileTypeFilter(query, 'FILES');
  query = addSessionIdsFilter(query, [ eventId ]);
  query = addFileSelectionsFilter(query, filenames);
  query = addFilenameFilter(query, filename);

  return promiseRequest({
    method: 'query',
    modelName: 'reconstruction-file-extract-job-id',
    query
  });
}

export default fetchFileExtractJobId;