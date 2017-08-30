import data from './data';

export default {
  subscriptionDestination: '/user/queue/endpoint/management/packageconfig/get',
  requestDestination: '/ws/endpoint/management/packageconfig/get',
  message(/* frame */) {
    return {
      data,
      meta: {
        complete: true
      }
    };
  }
};
