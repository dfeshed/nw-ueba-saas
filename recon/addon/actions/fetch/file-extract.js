import { lookup } from 'ember-dependency-lookup';
import {
  endpointFilter,
  addFileTypeFilter,
  addSessionIdsFilter,
  addFileSelectionsFilter,
  addFilenameFilter
} from '../util/query-util';
import { EVENT_TYPES } from 'component-lib/constants/event-types';
import { EVENT_DOWNLOAD_TYPES } from 'component-lib/constants/event-download-types';

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

// If extracting files from network event, extractType is the fileType (i.e. 'FILES')
// If extracting endpoint events, extractType is 'LOG'
// else extractType is eventType (eg. 'NETWORK', 'LOG')
const _getExtractType = (eventType, fileType) => {

  switch (eventType) {
    case EVENT_TYPES.LOG:
      return EVENT_DOWNLOAD_TYPES.LOG;
    case EVENT_TYPES.ENDPOINT:
      return EVENT_DOWNLOAD_TYPES.LOG;
    case EVENT_TYPES.NETWORK:
      if (fileType === EVENT_DOWNLOAD_TYPES.FILES) {
        return EVENT_DOWNLOAD_TYPES.FILES;
      }
      return EVENT_DOWNLOAD_TYPES.NETWORK;
    default:
      return eventType;
  }
};

export default function fetchExtractJobId(endpointId, eventId, fileType, filename, filenames, eventType) {
  const request = lookup('service:request');
  let query = endpointFilter(endpointId);
  query = addFilenameFilter(query, filename);
  query = addSessionIdsFilter(query, [ eventId ]);

  const extractType = _getExtractType(eventType, fileType);

  if (extractType === EVENT_DOWNLOAD_TYPES.FILES) {
    // downloading files requires list of filenames to be downloaded
    query = addFileSelectionsFilter(query, filenames);
  } else {
    // downloading 'PCAPS', 'PAYLOADS', etc from NETWORK events and 'TEXT', 'CSV', etc from LOG events requires fileType
    query = addFileTypeFilter(query, fileType);
  }

  // separate socket enpoint call based on extractType (eg. FILES, NETWORK, LOG)
  return request.promiseRequest({
    method: 'query',
    modelName: `reconstruction-extract-${extractType}-job-id`,
    query
  });
}
