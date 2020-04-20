import { util } from 'mock-server/index';

export default {
  subscriptionDestination: '/user/queue/investigate/meta/values',
  requestDestination: '/ws/investigate/meta/values/stream',
  page(frame, sendMessage) {

    const { body } = frame;
    const bodyParsed = JSON.parse(body);
    util.sendBatches({
      requestBody: bodyParsed,
      dataArray: [{
        value: 'Machine1'
      }],
      sendMessage,
      delayBetweenBatches: 50
    });
  }
};
