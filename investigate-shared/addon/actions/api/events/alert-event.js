import {
  streamingRequest,
  timeRangeFilter
} from './utils';

import moment from 'moment';

export default function(event, handlers) {
  const currentTimestamp = moment().unix();
  const query = {
    filter: [
      { field: 'endpointId', value: event.sourceId },
      timeRangeFilter('0', currentTimestamp),
      { field: 'query', value: `sessionid = ${event.id}` }
    ],
    stream: { limit: 100000, batch: 100000 }
  };

  return streamingRequest(
    'core-event',
    query,
    handlers
  );
}