import { createSendHeader, prepareMessage } from '../../../../util';

export default {
  subscriptionDestination: '/test/subscription/promise/_4',
  requestDestination: '/test/request/promise/_4',
  prepareSendMessage(frame) {
    const headers = createSendHeader(frame, this);
    // build body, include original request parsed into object
    const body = {
      code: 456,
      request: JSON.parse(frame.body)
    };
    const outMsg = prepareMessage(headers, body);
    return outMsg;
  }
};