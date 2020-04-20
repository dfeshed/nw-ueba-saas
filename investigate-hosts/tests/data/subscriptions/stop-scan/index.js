import data from './data';

export default {
  subscriptionDestination: '/user/queue/endpoint/command/stop-scan',
  requestDestination: '/ws/endpoint/command/stop-scan',
  message(/* frame */) {
    return {
      data,
      meta: {
        complete: true
      }
    };
  }
};
