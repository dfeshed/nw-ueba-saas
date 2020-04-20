export default {
  subscriptionDestination: '/user/queue/administration/global/set/user/preferences',
  requestDestination: '/ws/administration/global/set/user/preferences',
  message() {
    const data = { code: 0 };
    return {
      data,
      meta: {
        complete: true
      }
    };
  }
};
