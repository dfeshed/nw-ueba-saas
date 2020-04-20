import data from './data';

export default {
  subscriptionDestination: '/user/queue/endpoint/mft/get-records',
  requestDestination: '/ws/endpoint/mft/get-records',
  message(/* frame */) {
    return {
      data,
      meta: {
        complete: true
      }
    };
  }
};
