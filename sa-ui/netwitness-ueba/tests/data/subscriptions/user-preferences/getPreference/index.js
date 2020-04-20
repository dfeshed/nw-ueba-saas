export default {
  // Setting a delay shorter than getTimezones so that fetching of preferences happens before the timezone list. We
  // want to be able to test that setting the timezone from user preferences still works if the timezones come in later,
  // because the timezone service attempts to lookup the user pref timezone from the timezone list when it is being set
  // from preferences.
  delay: 50,
  subscriptionDestination: '/user/queue/administration/global/get/user/preferences',
  requestDestination: '/ws/administration/global/get/user/preferences',
  message() {
    return {
      code: 0,
      data: {
        'contextMenuEnabled': true,
        'defaultComponentUrl': '/respond',
        'userLocale': 'en_US',
        'dateFormat': 'MM/dd/yyyy',
        'timeFormat': 'HR12',
        'timeZone': 'America/Los_Angeles',
        'spacingType': 'LOOSE',
        'themeType': 'DARK',
        'notificationEnabled': true,
        'defaultInvestigatePage': '/navigate'
      }
    };
  }
};
