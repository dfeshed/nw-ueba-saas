import data from './data';

export default {
  subscriptionDestination: '/user/queue/risk/context/host',
  requestDestination: '/ws/respond/risk/context/host',
  message(/* frame */) {
    return {
      meta: {
        complete: true
      },
      data
    };
  }
};
