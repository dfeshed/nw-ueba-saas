import data from './data';

export default {
  subscriptionDestination: '/user/queue/endpoint/command/update-isolation-exclusionList',
  requestDestination: '/ws/endpoint/command/update-isolation-exclusionList',
  message(/* frame */) {
    return {
      data,
      meta: {
        complete: true
      }
    };
  }
};
