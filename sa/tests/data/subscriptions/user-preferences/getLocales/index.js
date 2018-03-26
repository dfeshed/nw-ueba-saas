export default {
  subscriptionDestination: '/user/queue/administration/locales/get',
  requestDestination: '/ws/administration/locales/get',
  message() {
    return {
      code: 0,
      data: [
        'spanish_es',
        'german_de'
      ]
    };
  }
};
