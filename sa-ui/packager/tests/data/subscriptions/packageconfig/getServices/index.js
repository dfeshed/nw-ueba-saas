import data from './data';

export default {
  subscriptionDestination: '/user/queue/endpoint/logconfig/servers',
  requestDestination: '/ws/endpoint/logconfig/servers',
  message(/* frame */) {
    return {
      data,
      meta: {
        complete: true
      }
    };
  }
};
