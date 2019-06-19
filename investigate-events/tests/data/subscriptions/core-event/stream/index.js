import { util } from 'mock-server';
import data from './data';

export default {
  subscriptionDestination: '/user/queue/investigate/events',
  requestDestination: '/ws/investigate/events/stream',
  page(frame, sendMessage) {
    const { body } = frame;
    const bodyParsed = JSON.parse(body);
    const { filter, stream: { limit } } = bodyParsed;
    const eventList = data();
    const query = (filter || []).find((ele) => ele.field === 'query');
    const searchTerm = (filter || []).find((ele) => ele.field === 'searchTerm');
    const queryValue = query && query.value;
    const searchTermValue = searchTerm && searchTerm.value;
    const match = queryValue ? String(queryValue).match(/\(sessionid > ([0-9]+)\)/) : null;
    const sessionId = match && parseInt(match[1], 10);
    let results;
    // If we query for a Free-Form Filter or Text Filter of "(limited)" or
    // "limited", respectively, send back zero results.
    if (queryValue === '(limited)' || searchTermValue === 'limited') {
      sendMessage({
        requestBody: bodyParsed,
        dataArray: [],
        meta: {
          percent: 100,
          complete: true
        }
      });
    } else {
      results = !sessionId ? eventList : eventList.filter(function(evt) {
        return evt.sessionId > sessionId;
      });
      results = limit ? results.slice(0, limit) : results;
      util.sendBatches({
        requestBody: bodyParsed,
        dataArray: results,
        sendMessage,
        delayBetweenBatches: 1000
      });
    }
  }
};
