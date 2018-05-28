export default {
  subscriptionDestination: '/user/queue/contexthub/context/list/save',
  requestDestination: '/ws/contexthub/context/list/save',
  canceltDestination: '/ws/contexthub/context/cancel',
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