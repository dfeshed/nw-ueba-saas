import { util } from 'mock-server';
import data from './data';

export default {
  subscriptionDestination: '/user/queue/investigate/meta/values',
  requestDestination: '/ws/investigate/meta/values/stream',
  page(frame, sendMessage) {
    const { body } = frame;
    const bodyParsed = JSON.parse(body);
    const eventList = data();

    util.sendBatches({
      requestBody: bodyParsed,
      dataArray: eventList,
      sendMessage,
      delayBetweenBatches: 50
    });
  }
};