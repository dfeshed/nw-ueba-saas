const data = ['Access Control', 'Wireless Devices', 'Intrusion'];

export default {
  subscriptionDestination: '/user/queue/content/parser/device/class',
  requestDestination: '/ws/content/parser/device/class',
  message(/* frame */) {
    return {
      data,
      'meta': {
        'total': 3
      }
    };
  }
};
