import data from './data';

export default {
  subscriptionDestination: '/user/queue/endpoint/packager/get',
  requestDestination: '/ws/endpoint/packager/get',
  message(/* frame */) {
    return {
      data,
      meta: {
        complete: true
      }
    };
  }
};
