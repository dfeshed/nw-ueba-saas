import data from './data';

export default {
  subscriptionDestination: '/user/queue/risk/context/detail/host',
  requestDestination: '/ws/respond/risk/context/detail/host',
  message(/* frame */) {
    return {
      meta: {
        complete: true
      },
      data
    };
  }
};
