export default {
  subscriptionDestination: '/user/queue/endpoint/download/delete',
  requestDestination: '/ws/endpoint/download/delete',
  message(/* frame */) {
    // return doesn't matter, endpoint needs to return to
    // avoid UI hanging
    return {
    };
  }
};
