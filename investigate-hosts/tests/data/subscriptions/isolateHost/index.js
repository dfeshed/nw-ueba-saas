import data from './data';

export default {
  subscriptionDestination: '/user/queue/endpoint/command/start-isolation',
  requestDestination: '/ws/endpoint/command/start-isolation',
  message(/* frame */) {
    return {
      data,
      meta: {
        complete: true
      }
    };
  }
};
