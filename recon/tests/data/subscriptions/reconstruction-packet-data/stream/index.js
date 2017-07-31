import { util } from 'mock-server';
import allData from './data';

export default {
  subscriptionDestination: '/user/queue/investigate/reconstruct/session-packets',
  requestDestination: '/ws/investigate/reconstruct/session-packets/stream',
  page(frame, sendMessage) {
    const requestBody = JSON.parse(frame.body);
    util.sendBatches({
      requestBody,
      dataArray: allData,
      sendMessage,
      delayBetweenBatches: 500
    });
  }
};