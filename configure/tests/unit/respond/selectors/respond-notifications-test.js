import { module, test } from 'qunit';
import notifications from '../../../data/subscriptions/notification-settings/findAll/data';
import {
  getEmailServers,
  getEnabledEmailServers,
  getSocManagerEmailAddresses,
  getSelectedEmailServer,
  getSelectedEmailServerId,
  getNotificationSettings,
  getNotificationsStatus,
  isTransactionUnderway,
  hasSocManagerEmails,
  isMissingRequiredData
} from 'configure/reducers/respond/notifications/selectors';

module('Unit | Utility | Respond Notifications Selectors');

const state = {
  configure: {
    respond: {
      notifications: {
        ...notifications,
        selectedEmailServer: 'my-favorite-server',
        notificationsStatus: 'wait',
        isTransactionUnderway: true
      }
    }
  }
};

test('Basic notifications selectors', function(assert) {
  const enabledServers = [
    {
      id: 'my-email-server',
      name: 'My Email Server',
      description: 'My email server description',
      enabled: true
    },
    {
      id: 'my-favorite-server',
      name: 'My Favorite Server',
      description: 'My favorite email server description',
      enabled: true
    }
  ];
  const selectedEmailServer = {
    id: 'my-favorite-server',
    name: 'My Favorite Server',
    description: 'My favorite email server description',
    enabled: true
  };
  assert.deepEqual(getEmailServers(state), notifications.emailServers);
  assert.deepEqual(getEnabledEmailServers(state), enabledServers, 'Only the enabled servers are returned');
  assert.deepEqual(getSocManagerEmailAddresses(state), ['soc@rsa.com']);
  assert.deepEqual(getSelectedEmailServer(state), selectedEmailServer);
  assert.equal(getSelectedEmailServerId(state), 'my-favorite-server');
  assert.equal(getNotificationSettings(state), notifications.notificationSettings);
  assert.equal(getNotificationsStatus(state), 'wait');
  assert.equal(isTransactionUnderway(state), true);
  assert.equal(hasSocManagerEmails(state), true);
  assert.equal(isMissingRequiredData(state), false);
});