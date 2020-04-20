import data from './data';

export default {
  subscriptionDestination: '/user/queue/content/parser/update',
  requestDestination: '/ws/content/parser/update',
  message(/* frame */) {
    return {
      code: 0,
      data
    };
  }
};
