import data from './data';

export default {
  subscriptionDestination: '/user/queue/endpoint/servers',
  requestDestination: '/ws/endpoint/servers',
  message(/* frame */) {
    return {
      data,
      meta: {
        complete: true
      }
    };
  }
};