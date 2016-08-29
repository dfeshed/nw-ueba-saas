import { prepareMessage } from 'mock-server';

export default {
  subscriptionDestination: '/test/subscription/promise/_1',
  requestDestination: '/test/request/promise/_1',
  createSendMessage(frame) {
    const body = {
      code: 0,
      data: [1, 1, 1, 1, 1],
      request: JSON.parse(frame.body)
    };
    return prepareMessage(this, frame, body);
  }
};