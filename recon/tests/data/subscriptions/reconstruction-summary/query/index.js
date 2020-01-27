import { withPayloads } from './data';

export default {
  subscriptionDestination: '/user/queue/investigate/reconstruct/session-summary',
  requestDestination: '/ws/investigate/reconstruct/session-summary',
  delay: 1,
  message(/* frame */) {
    return {
      meta: {
        complete: true
      },
      data: { ...withPayloads }
    };
  }
};
