import { util } from 'mock-server';
import data from './data';

// Creates a filter function from a given filter array of POJOs.
// Currently only supports filtering by alert ID (`_id`). Any other filters are ignored.
// If there are no supported filters in the input, just returns null.
function makeFilterFunction(filterArray) {

  // Does the given filter include an `_id` field?
  const idCondition = (filterArray || []).find(({ field }) => {
    return field === '_id';
  });

  if (idCondition) {
    // Return a function that matches a record's `id` with the given `_id` filter value.
    const targetId = idCondition.value;
    return function(record) {
      return record.id === targetId;
    };
  } else {
    // No `_id` filter found.
    // We don't support any other filtering here yet, so return empty filter.
    return null;
  }
}

export default {
  subscriptionDestination: '/user/queue/alerts',
  requestDestination: '/ws/response/alerts',

  page(frame, sendMessage) {
    const { body } = frame;
    const bodyParsed = JSON.parse(body);
    const { filter } = bodyParsed;
    const filterFunction = makeFilterFunction(filter);
    const filteredData = filterFunction ? data.filter(filterFunction) : data;
    const { stream: { limit } } = bodyParsed;
    const results = limit ? filteredData.slice(0, limit) : filteredData;

    util.sendBatches({
      requestBody: bodyParsed,
      dataArray: results,
      sendMessage,
      delayBetweenBatches: 50
    });
  }
};