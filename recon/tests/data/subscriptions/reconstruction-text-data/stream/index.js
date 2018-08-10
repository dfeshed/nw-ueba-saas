import { util } from 'mock-server';
import decodedData from './decodedData';
import encodedData from './encodedData';
import endpointData from './endpointData';

export default {
  subscriptionDestination: '/user/queue/investigate/reconstruct/session-text',
  requestDestination: '/ws/investigate/reconstruct/session-text/stream',
  page(frame, sendMessage) {
    const requestBody = JSON.parse(frame.body);
    const { filter } = requestBody;
    const sessionId = filter.find((obj) => obj.field === 'sessionId');
    const decode = filter.find((obj) => obj.field === 'decode');
    let data;
    if (sessionId && sessionId.value % 3 == 0) {
      data = endpointData;
    } else {
      data = decode.value ? decodedData : encodedData;
    }

    util.sendBatches({
      requestBody,
      dataArray: data,
      sendMessage,
      delayBetweenBatches: 500
    });
  }
};