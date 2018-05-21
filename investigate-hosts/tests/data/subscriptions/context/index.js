import data from './data';

export default {
  subscriptionDestination: '/user/queue/administration/context/lookup',
  requestDestination: '/ws/administration/context/lookup',
  message(/* frame */) {
    return {
      data,
      meta: {
        complete: true
      }
    };
  }
};