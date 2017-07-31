import data from './data';

export default {
  subscriptionDestination: '/user/queue/administration/context/list',
  requestDestination: '/ws/administration/context/list',
  message(/* frame */) {
    return {
      data,
      meta: {
        complete: true
      }
    };
  }
};
