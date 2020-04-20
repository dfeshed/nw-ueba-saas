import {
  streamingRequest,
  timeRangeFilter
} from './utils';

import _ from 'lodash';
import moment from 'moment';

export default function(events, handlers) {
  const groupedEvents = _.groupBy(events, 'sourceId');
  Object.keys(groupedEvents).map((endpointId) => {
    const currentTimestamp = moment().unix();
    const param = groupedEvents[endpointId].mapBy('id').join();
    const query = {
      filter: [
        { field: 'endpointId', value: endpointId },
        timeRangeFilter('0', currentTimestamp),
        { field: 'query', value: `sessionid = ${param}` }
      ],
      stream: { limit: 100000, batch: 100000 }
    };
    return streamingRequest(
      'core-event',
      query,
      handlers
    );
  });
}