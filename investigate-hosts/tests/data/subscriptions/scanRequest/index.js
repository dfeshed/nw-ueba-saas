import data from './data';

export default {
  subscriptionDestination: '/user/queue/endpoint/command/start-scan',
  requestDestination: '/ws/endpoint/command/start-scan',
  message(/* frame */) {
    return {
      data,
      meta: {
        complete: true
      }
    };
  }
};
