import { lookup } from 'ember-dependency-lookup';

/**
 * Executes a websocket fetch call for the respond notification settings
 * @method getNotificationSettings
 * @public
 * @returns {*}
 */
function getNotificationSettings() {
  const request = lookup('service:request');
  return request.promiseRequest({
    method: 'findAll',
    modelName: 'notification-settings',
    query: {}
  });
}

function updateNotificationSettings(notificationSettings) {
  const request = lookup('service:request');
  return request.promiseRequest({
    method: 'updateRecord',
    modelName: 'notification-settings',
    query: {
      ...notificationSettings
    }
  });
}

export default {
  getNotificationSettings,
  updateNotificationSettings
};
