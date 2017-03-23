export default {
  subscriptionDestination: '/user/queue/administration/context/list/create',
  requestDestination: '/ws/administration/context/list/create',
  canceltDestination: '/ws/administration/context/cancel',
  count: 0,
  message(/* frame */) {
    const data = { code: 0 };
    return {
      data,
      meta: {
        complete: true
      }
    };
  }
};
