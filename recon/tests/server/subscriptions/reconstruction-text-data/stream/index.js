import { util } from 'mock-server';
import decodedData from './decodedData';
import encodedData from './encodedData';

export default {
  subscriptionDestination: '/user/queue/investigate/reconstruct/session-text',
  requestDestination: '/ws/investigate/reconstruct/session-text/stream',
  page(frame, sendMessage) {
    const requestBody = JSON.parse(frame.body);
    const { filter } = requestBody;
    const decode = filter.find((obj) => obj.field === 'decode');
    const data = decode.value ? decodedData : encodedData;

    util.sendBatches({
      requestBody,
      dataArray: data,
      sendMessage,
      delayBetweenBatches: 500
    });
  }
};