export default {
  subscriptionDestination: '/user/queue/administration/timezones/get',
  requestDestination: '/ws/administration/timezones/get',
  message() {
    return {
      code: 0,
      data: [{
        'displayLabel': 'UTC (GMT+00:00)',
        'offset': 'GMT+00:00',
        'zoneId': 'UTC'
      }]
    };
  }
};
