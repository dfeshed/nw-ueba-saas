export default {
  // Set a delay so that the timezone list comes in after the the user preferences. Technically setting the timezone
  // from user preferences depends on the timezones already being loaded. This helps to test the case where timezones
  // come in later than preferences.
  delay: 2000,
  subscriptionDestination: '/user/queue/administration/timezones/get',
  requestDestination: '/ws/administration/timezones/get',
  message() {
    return {
      code: 0,
      data: [{
        'displayLabel': 'UTC (GMT+00:00)',
        'offset': 'GMT+00:00',
        'zoneId': 'UTC'
      }, {
        'displayLabel': 'Los Angeles (GMT-07:00)',
        'offset': 'GMT-07:00',
        'zoneId': 'America/Los_Angeles'
      }]
    };
  }
};
