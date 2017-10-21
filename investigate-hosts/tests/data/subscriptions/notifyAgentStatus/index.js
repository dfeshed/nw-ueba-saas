import data from './data';

export default {
  subscriptionDestination: '/topic/agentstatus/notifications',
  requestDestination: '/dummy/agentstatus/notifications',
  cancelDestination: '/ws/endpoint/agent/cancel',
  message(/* frame */) {
    return {
      data,
      meta: {
        complete: true
      }
    };
  }
};
