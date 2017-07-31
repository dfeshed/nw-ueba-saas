import data from './data';

export default {
  subscriptionDestination: '/user/queue/administration/context/data-sources',
  requestDestination: '/ws/administration/context/data-sources',
  message(/* frame */) {
    return {
      data,
      meta: {
        complete: true
      }
    };
  }
};