import data from './data';

export default {
  subscriptionDestination: '/user/queue/endpoint/data/propertypanel/data',
  requestDestination: '/ws/endpoint/data/propertypanel/data',
  message(/* frame */) {
    return {
      data,
      meta: {
        complete: true
      }
    };
  }
};
