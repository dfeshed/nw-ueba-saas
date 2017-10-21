import data from './data';

export default {
  subscriptionDestination: '/user/queue/endpoint/agent/command/scan',
  requestDestination: '/ws/endpoint/agent/command/scan',
  message(/* frame */) {
    return {
      data,
      meta: {
        complete: true
      }
    };
  }
};
