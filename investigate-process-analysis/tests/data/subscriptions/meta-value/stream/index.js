import { util } from 'mock-server';
import data from './data';

export default {
  subscriptionDestination: '/user/queue/investigate/meta/values',
  requestDestination: '/ws/investigate/meta/values/stream',
  page(frame, sendMessage) {
    let eventList = [];
    const { body } = frame;
    const bodyParsed = JSON.parse(body);
    const metaFilter = (bodyParsed.filter || []).find((ele) => ele.field === 'metaName') || {};
    const metaName = metaFilter.value;
    if (metaName === 'alias.host') {
      eventList = data();
    } else if (metaName === 'agent.id') {
      eventList = [{
        value: '123456789',
        count: 2
      }];
    }
    util.sendBatches({
      requestBody: bodyParsed,
      dataArray: eventList,
      sendMessage,
      delayBetweenBatches: 50
    });
  }
};
