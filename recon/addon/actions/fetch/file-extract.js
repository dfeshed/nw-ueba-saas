import { lookup } from 'ember-dependency-lookup';
import {
  endpointFilter,
  addFileTypeFilter,
  addSessionIdsFilter,
  addFileSelectionsFilter,
  addFilenameFilter,
  addEventTypeFilter
} from '../util/query-util';

/**
 * Retrieves an ID from server for a job to extract the files of a given event id.
 * Note: For now, always requests all the event's files (whether selected or not).
 * @param endpointId
 * @param eventId
 * @param fileType - The type to extract (PCAP, FILES, LOG)
 * @param filename - Name for the downloaded ZIP file.
 * @param filenames
 * @returns Promise that will resolve with the server response. The response will
 * NOT include the contents of the requested file(s). Instead, the response
 * (if successful) include a "job" id.  This job can then be monitored with
 * other server calls. If & when the job is completed, the job will yield a
 * download URL, which can be used to fetch the actual files (zipped).
 * @public
 */
export default function fetchExtractJobId(endpointId, eventId, fileType, filename, filenames, eventType) {
  const request = lookup('service:request');
  let query = endpointFilter(endpointId);
  query = addSessionIdsFilter(query, [ eventId ]);
  query = addFilenameFilter(query, filename);
  query = addFileTypeFilter(query, fileType);
  query = addEventTypeFilter(query, eventType);

  if (fileType !== 'LOG') {
    query = addFileSelectionsFilter(query, filenames);
  }

  return request.promiseRequest({
    method: 'query',
    modelName: 'reconstruction-extract-job-id',
    query
  });
}
