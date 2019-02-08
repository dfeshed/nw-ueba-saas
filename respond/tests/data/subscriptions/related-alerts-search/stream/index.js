import data from './data';

export default {
  delay: 1,
  subscriptionDestination: '/user/queue/related/alerts',
  requestDestination: '/ws/respond/related/alerts',
  message(/* frame */) {
    return {
      code: 0,
      data,
      meta: {
        complete: true
      }
    };
  }
};
