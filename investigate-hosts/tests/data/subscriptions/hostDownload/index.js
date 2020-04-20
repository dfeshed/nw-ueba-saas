import data from './data';

export default {
  subscriptionDestination: '/user/queue/endpoint/download/search',
  requestDestination: '/ws/endpoint/download/search',
  message(/* frame */) {
    // return doesn't matter, endpoint needs to return to
    // avoid UI hanging
    return {
      data,
      meta: {
        complete: true
      }
    };
  }
};
