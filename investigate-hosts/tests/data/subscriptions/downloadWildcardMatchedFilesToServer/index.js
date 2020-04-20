export default {
  subscriptionDestination: '/user/queue/endpoint/command/download-files',
  requestDestination: '/ws/endpoint/command/download-files',
  message(/* frame */) {
    // return doesn't matter, endpoint needs to return to
    // avoid UI hanging
    return {
    };
  }
};
