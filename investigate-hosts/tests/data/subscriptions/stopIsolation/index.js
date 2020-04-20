import data from './data';

export default {
  subscriptionDestination: '/user/queue/endpoint/command/stop-isolation',
  requestDestination: '/ws/endpoint/command/stop-isolation',
  message(/* frame */) {
    return {
      data,
      meta: {
        complete: true
      }
    };
  }
};
