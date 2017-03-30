export default {
  subscriptionDestination: '/user/queue/administration/context/get/liveconnect/userprefs',
  requestDestination: '/ws/administration/context/get/liveconnect/userprefs',
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
