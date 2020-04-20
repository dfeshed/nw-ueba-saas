export default {
  subscriptionDestination: '/user/queue/endpoint/rar/installer/create',
  requestDestination: '/ws/endpoint/rar/installer/create',
  message(/* frame */) {
    return {
      data: { id: 'e3eewr' },
      meta: {
        complete: true
      }
    };
  }
};
