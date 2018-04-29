import data from './data';

export default {
  subscriptionDestination: '/user/queue/endpoint/file/get',
  requestDestination: '/ws/endpoint/file/get',
  message(/* frame */) {
    return {
      data,
      meta: {
        complete: true
      }
    };
  }
};
