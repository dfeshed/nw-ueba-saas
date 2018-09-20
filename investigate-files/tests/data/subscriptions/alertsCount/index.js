import data from './data';
export default {
  subscriptionDestination: '/user/queue/scoring/scoring-server',
  requestDestination: '/user/queue/scoring/scoring-server',
  message(/* frame */) {
    return {
      meta: {
        complete: true
      },
      data
    };
  }
};
