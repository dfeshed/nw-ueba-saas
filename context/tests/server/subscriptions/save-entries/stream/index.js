export default {
  subscriptionDestination: '/user/queue/administration/context/list/save',
  requestDestination: '/ws/administration/context/list/save',
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