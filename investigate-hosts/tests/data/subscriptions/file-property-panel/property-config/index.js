import data from './data';

export default {
  subscriptionDestination: '/user/queue/endpoint/data/propertypanel/config',
  requestDestination: '/ws/endpoint/data/propertypanel/config',
  message(/* frame */) {
    return {
      data,
      meta: {
        complete: true
      }
    };
  }
};
