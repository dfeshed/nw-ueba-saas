export default {
  subscriptionDestination: '/user/queue/endpoint/memory/export',
  requestDestination: '/ws/endpoint/memory/export',
  message(/* frame */) {
    // return doesn't matter, endpoint needs to return to
    // avoid UI hanging
    return {
    };
  }
};
