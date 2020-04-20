import data from './data';

export default {
  subscriptionDestination: '/user/queue/notifications',
  requestDestination: '/ws/respond/notifications',
  message(/* frame */) {
    return {
      ...data
    };
  }
};
