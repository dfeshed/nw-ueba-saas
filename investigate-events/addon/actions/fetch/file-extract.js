// *******
// BEGIN - Copy/pasted & modified download code from Recon
// *******
import { lookup } from 'ember-dependency-lookup';
import {
  endpointFilter,
  addFileTypeFilter,
  addSessionIdsFilter,
  addFilenameFilter,
  addQueryFilters,
  addTimerangeFilter,
  addMetaToDownloadFilter,
  encodeMetaFilterConditions
} from 'investigate-shared/actions/api/events/utils';

/**
 * Retrieves an ID from server for a job to extract the files of a given event id.
 * Note: For now, always requests all the event's files (whether selected or not).w
 * @param endpointId
 * @param eventIds
 * @param fileType - The type to extract (PCAP, CSV, LOG)
 * @param filename - Name for the downloaded file.
 * @returns Promise that will resolve with the server response. The response will
 * NOT include the contents of the requested file(s). Instead, the response
 * (if successful) include a "job" id.  This job can then be monitored with
 * other server calls. If & when the job is completed, the job will yield a
 * download URL, which can be used to fetch the actual files
 * @public
 */
export default function fetchExtractJobId(queryNode, endpointId, eventIds, fileType, filename, eventDownloadType, isSelectAll, columnList) {
  const request = lookup('service:request');

  let query = endpointFilter(endpointId);
  query = addFilenameFilter(query, filename);
  query = addFileTypeFilter(query, fileType);

  if (isSelectAll) {
    const filters = queryNode.metaFilter.conditions || queryNode.metaFilter;
    query = addQueryFilters(query, encodeMetaFilterConditions(filters));
    query = addTimerangeFilter(query, queryNode.startTime, queryNode.endTime);
  } else {
    query = addSessionIdsFilter(query, eventIds);
  }
  query = addMetaToDownloadFilter(query, columnList);

  // separate socket enpoint call based on eventDownloadType (eg. META, NETWORK, LOG)
  return request.promiseRequest({
    method: 'query',
    modelName: `extract-${eventDownloadType}-job-id`,
    query
  });
}
// *******
// END - Copy/pasted & modified download code from Recon
// *******
