import data from './data';

export default {
  delay: 1,
  subscriptionDestination: '/user/queue/incident/details',
  requestDestination: '/ws/respond/incident/details',
  message(/* frame */) {
    return {
      data
    };
  }
};
