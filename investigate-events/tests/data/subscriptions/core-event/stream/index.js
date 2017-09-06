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
    const queryValue = query && query.value;
    const match = queryValue ? String(queryValue).match(/\(sessionid > ([0-9]+)\)/) : null;
    const sessionId = match && parseInt(match[1], 10);
    let results = !sessionId ? eventList : eventList.filter(function(evt) {
      return evt.sessionId > sessionId;
    });
    results = limit ? results.slice(0, limit) : results;

    util.sendBatches({
      requestBody: bodyParsed,
      dataArray: results,
      sendMessage,
      delayBetweenBatches: 50
    });
  }
};