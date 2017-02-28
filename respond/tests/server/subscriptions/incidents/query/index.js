import data from './data';

export default {
  subscriptionDestination: '/user/queue/incidents',
  requestDestination: '/ws/response/incidents',
  message(/* frame */) {
    return {
      data,
      meta: {
        total: 1099
      }
    };
  }
};