import { data } from './data';

export default {
  subscriptionDestination: '/user/queue/investigate/reconstruct/session-meta',
  requestDestination: '/ws/investigate/reconstruct/session-meta',
  delay: 1,
  message(/* frame */) {
    return {
      data
    };
  }
};
