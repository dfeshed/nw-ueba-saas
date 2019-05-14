export default {
  subscriptionDestination: '/user/queue/endpoint/command/download-mft',
  requestDestination: '/ws/endpoint/command/download-mft',
  message(/* frame */) {
    // return doesn't matter, endpoint needs to return to
    // avoid UI hanging
    return {
    };
  }
};
