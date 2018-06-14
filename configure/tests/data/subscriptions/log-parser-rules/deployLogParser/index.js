import data from './data';
export default {
  subscriptionDestination: '/user/queue/content/parser/deploy',
  requestDestination: '/ws/content/parser/deploy',
  message(/* frame */) {
    return {
      ...data
    };
  }
};