export default {
  subscriptionDestination: '/user/queue/contexthub/context/get/liveconnect/userprefs',
  requestDestination: '/ws/contexthub/context/get/liveconnect/userprefs',
  count: 0,
  message(/* frame */) {
    return {
      code: 0,
      data: { skillLevel: 3 },
      meta: {
        complete: true
      }
    };
  }
};
