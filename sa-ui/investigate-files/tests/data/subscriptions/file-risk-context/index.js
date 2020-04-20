import data from './data';

export default {
  subscriptionDestination: '/user/queue/risk/context/file',
  requestDestination: '/ws/respond/risk/context/file',
  message(/* frame */) {
    return {
      meta: {
        complete: true
      },
      data
    };
  }
};
