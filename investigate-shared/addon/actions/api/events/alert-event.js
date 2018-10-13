import {
  streamingRequest,
  timeRangeFilter
} from './utils';

import moment from 'moment';

export default function(context, handlers) {
  Object.keys(context).map((endpointId) => {
    const currentTimestamp = moment().unix();
    const param = context[endpointId].mapBy('id').join();
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