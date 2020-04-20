export default {
  subscriptionDestination: '/user/queue/endpoint/command/download-file',
  requestDestination: '/ws/endpoint/command/download-file',
  message(/* frame */) {
    // return doesn't matter, endpoint needs to return to
    // avoid UI hanging
    return {
    };
  }
};
