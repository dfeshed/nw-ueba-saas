import data from './data';

export default {
  subscriptionDestination: '/user/queue/aggregation/rule',
  requestDestination: '/ws/respond/aggregation/rule',
  message(/* frame */) {
    return {
      data
    };
  }
};