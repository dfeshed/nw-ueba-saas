import { util } from 'mock-server';
import data from './data';

export default {
  subscriptionDestination: '/user/queue/investigate/events',
  requestDestination: '/ws/investigate/events/stream',
  page(frame, sendMessage) {
    const { body } = frame;
    const bodyParsed = JSON.parse(body);
    const { filter } = bodyParsed;

    const eventList = data();
    const query = (filter || []).find((ele) => ele.field === 'query');
    const queryValue = query && query.value;
    const childAndParent = queryValue.indexOf('vid.src') && queryValue.valueOf('vid.dst');

    const match = queryValue ? String(queryValue).match(/vid.src = ([0-9]+)?/i) : null;
    const vid = match && match[1];
    const results = !vid ? eventList : eventList.filter(function(evt) {
      const data = hasherizeEventMeta(evt);
      if (childAndParent) {
        return data['vid.dst'] === vid || data['vid.src'] === vid;
      } else {
        return data['vid.dst'] === vid;
      }
    });

    util.sendBatches({
      requestBody: bodyParsed,
      dataArray: results,
      sendMessage,
      delayBetweenBatches: 50
    });
  }
};

const hasherizeEventMeta = (event) => {
  if (event) {
    const { metas } = event;
    if (!metas) {
      return;
    }
    const len = (metas && metas.length) || 0;
    let i;
    for (i = 0; i < len; i++) {
      const meta = metas[i];
      event[meta[0]] = meta[1];
    }
    event.childCount = 0;
    return event;
  }
  return event;
};
