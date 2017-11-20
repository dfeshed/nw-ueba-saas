export default {
  subscriptionDestination: '/user/queue/administration/global/get/user/preferences',
  requestDestination: '/ws/administration/global/get/user/preferences',
  message() {
    return {
      code: 0,
      data: {
        'contextMenuEnabled': true,
        'defaultComponentUrl': '/respond',
        'userLocale': 'JA',
        'dateFormat': 'MM/dd/yyyy',
        'timeFormat': 'HR12',
        'timeZone': 'UTC',
        'spacingType': 'LOOSE',
        'themeType': 'DARK',
        'notificationEnabled': true
      }
    };
  }
};
