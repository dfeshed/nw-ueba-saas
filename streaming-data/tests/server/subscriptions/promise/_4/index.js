import { prepareMessage } from 'mock-server';

export default {
  subscriptionDestination: '/test/subscription/promise/_4',
  requestDestination: '/test/request/promise/_4',
  createSendMessage(frame) {
    const body = {
      code: 456,
      request: JSON.parse(frame.body)
    };
    return prepareMessage(this, frame, body);
  }
};