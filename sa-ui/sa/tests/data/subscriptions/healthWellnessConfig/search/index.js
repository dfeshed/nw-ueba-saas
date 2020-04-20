import data from './data';

export default {
  subscriptionDestination: '/user/queue/metrics/monitor/get-all',
  requestDestination: '/ws/metrics/monitor/get-all',
  message(/* frame */) {
    return {
      data,
      meta: {
        complete: true
      }
    };
  }
};
