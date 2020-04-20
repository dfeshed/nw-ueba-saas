import data from './data';

export default {
  subscriptionDestination: '/user/queue/endpoint/logconfig/load',
  requestDestination: '/ws/endpoint/logconfig/load',
  message(/* frame */) {
    return {
      data,
      meta: {
        complete: true
      }
    };
  }
};
