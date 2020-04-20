import data from './data';

export default {
  subscriptionDestination: '/user/queue/risk/context/detail/file',
  requestDestination: '/ws/respond/risk/context/detail/file',
  message(/* frame */) {
    return {
      meta: {
        complete: true
      },
      data
    };
  }
};
