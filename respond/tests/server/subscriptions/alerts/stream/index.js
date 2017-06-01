import { util } from 'mock-server';
import data from './data';

export default {
  subscriptionDestination: '/user/queue/alerts',
  requestDestination: '/ws/response/alerts',

  page(frame, sendMessage) {
    const { body } = frame;
    const bodyParsed = JSON.parse(body);
    const { stream: { limit } } = bodyParsed;
    const results = limit ? data.slice(0, limit) : data;

    util.sendBatches({
      requestBody: bodyParsed,
      dataArray: results,
      sendMessage,
      delayBetweenBatches: 50
    });
  }
};