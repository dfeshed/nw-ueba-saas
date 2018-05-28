import data from './data';

export default {
  subscriptionDestination: '/user/queue/endpoint/insights-policy/get',
  requestDestination: '/ws/endpoint/insights-policy/get',
  message(/* frame */) {
    return {
      data,
      meta: {
        complete: true
      }
    };
  }
};
