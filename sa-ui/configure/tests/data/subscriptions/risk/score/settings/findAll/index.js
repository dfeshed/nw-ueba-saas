import data from './data';

export default {
  delay: 1,
  subscriptionDestination: '/user/queue/risk/score/settings',
  requestDestination: '/ws/respond/risk/score/settings',
  message(/* frame */) {
    return {
      code: 0,
      data
    };
  }
};
