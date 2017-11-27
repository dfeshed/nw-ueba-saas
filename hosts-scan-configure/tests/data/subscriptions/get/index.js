import data from './data';

export default {
  subscriptionDestination: '/user/queue/endpoint/policy/get',
  requestDestination: '/ws/endpoint/policy/get',
  message(/* frame */) {
    return {
      data,
      meta: {
        complete: true
      }
    };
  }
};
