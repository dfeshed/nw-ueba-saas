export default {
  subscriptionDestination: '/user/queue/endpoint/command/request-system-dump',
  requestDestination: '/ws/endpoint/command/request-system-dump',
  message(/* frame */) {
    // return doesn't matter, endpoint needs to return to
    // avoid UI hanging
    return {
    };
  }
};
