import { createSendHeader, prepareMessage } from '../../../../util';

export default {
  subscriptionDestination: '/test/subscription/promise/_3',
  requestDestination: '/test/request/promise/_3',
  prepareSendMessage(frame) {
    const headers = createSendHeader(frame, this);
    // build body, include original request parsed into object
    const body = {
      code: 0,
      data: [1, 1, 1, 1, 1],
      request: JSON.parse(frame.body)
    };
    const outMsg = prepareMessage(headers, body);
    return outMsg;
  }
};