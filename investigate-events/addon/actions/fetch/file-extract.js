// *******
// BEGIN - Copy/pasted download code from Recon
// *******
import { lookup } from 'ember-dependency-lookup';
import {
  endpointFilter,
  addFileTypeFilter,
  addSessionIdsFilter,
  addFilenameFilter,
  addEventTypeFilter,
  addQueryFilters,
  addTimerangeFilter,
  encodeMetaFilterConditions
} from 'investigate-shared/actions/api/events/utils';

/**
 * Retrieves an ID from server for a job to extract the files of a given event id.
 * Note: For now, always requests all the event's files (whether selected or not).w
 * @param endpointId
 * @param eventIds
 * @param fileType - The type to extract (PCAP, FILES, LOG)
 * @param filename - Name for the downloaded ZIP file.
 * @returns Promise that will resolve with the server response. The response will
 * NOT include the contents of the requested file(s). Instead, the response
 * (if successful) include a "job" id.  This job can then be monitored with
 * other server calls. If & when the job is completed, the job will yield a
 * download URL, which can be used to fetch the actual files (zipped).
 * @public
 */
export default function fetchExtractJobId(queryNode, endpointId, eventIds, fileType, filename, eventType, isSelectAll) {
  const request = lookup('service:request');

  let query = endpointFilter(endpointId);
  query = addFilenameFilter(query, filename);
  query = addFileTypeFilter(query, fileType);
  query = addEventTypeFilter(query, eventType);

  if (isSelectAll) {
    const filters = queryNode.metaFilter.conditions || queryNode.metaFilter;
    query = addQueryFilters(query, encodeMetaFilterConditions(filters));
    query = addTimerangeFilter(query, queryNode.startTime, queryNode.endTime);
  } else {
    query = addSessionIdsFilter(query, eventIds);
  }

  return request.promiseRequest({
    method: 'query',
    modelName: 'reconstruction-extract-job-id',
    query
  });
}
// *******
// END - Copy/pasted download code from Recon
// *******