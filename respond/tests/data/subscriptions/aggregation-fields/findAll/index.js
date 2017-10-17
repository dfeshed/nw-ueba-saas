import data from './data';

export default {
  subscriptionDestination: '/user/queue/aggregation/fields',
  requestDestination: '/ws/respond/aggregation/fields',
  message(/* frame */) {
    return {
      data
    };
  }
};
