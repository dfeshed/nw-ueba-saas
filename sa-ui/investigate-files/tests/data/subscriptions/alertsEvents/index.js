import data from './data';

export default {
  delay: 1,
  subscriptionDestination: '/user/queue/alerts/events',
  requestDestination: '/ws/respond/alerts/events',
  message() {
    return {
      data
    };
  }
};
