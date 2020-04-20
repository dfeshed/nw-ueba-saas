import data from './data';

export default {
  subscriptionDestination: '/user/queue/endpoint/investigate/servers',
  requestDestination: '/ws/endpoint/investigate/servers',
  message(/* frame */) {
    return {
      data,
      meta: {
        complete: true
      }
    };
  }
};
