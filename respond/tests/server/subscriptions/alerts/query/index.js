import data from './data';

export default {
  subscriptionDestination: '/user/queue/alerts',
  requestDestination: '/ws/response/alerts',
  message(/* frame */) {
    return {
      data
    };
  }
};