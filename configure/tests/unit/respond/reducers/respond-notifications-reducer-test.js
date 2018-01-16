import Immutable from 'seamless-immutable';
import { module, test } from 'qunit';
import ACTION_TYPES from 'configure/actions/types/respond';
import reducer from 'configure/reducers/respond/notifications/reducer';
import notifications from '../../../data/subscriptions/notification-settings/findAll/data';

module('Unit | Utility | Respond Notifications Reducers');

const initialState = {
  emailServers: [],
  selectedEmailServer: null,
  notificationsStatus: null, // wait, completed, error
  socManagers: [],
  notificationSettings: [],
  isTransactionUnderway: false
};

test('With FETCH_NOTIFICATION_SETTINGS_STARTED, the state is properly set', function(assert) {
  const expectedEndState = {
    ...initialState,
    notificationsStatus: 'wait'
  };

  const endState = reducer(Immutable.from(initialState), {
    type: ACTION_TYPES.FETCH_NOTIFICATION_SETTINGS_STARTED
  });
  assert.deepEqual(endState, expectedEndState);
});

test('With FETCH_NOTIFICATION_SETTINGS, the state is properly updated', function(assert) {
  const action = {
    type: ACTION_TYPES.FETCH_NOTIFICATION_SETTINGS,
    payload: notifications
  };

  const expectedEndState = {
    ...initialState,
    notificationsStatus: 'completed',
    emailServers: notifications.emailServers,
    selectedEmailServer: notifications.selectedEmailServer,
    socManagers: notifications.socManagers,
    notificationSettings: notifications.notificationSettings
  };

  const endState = reducer(Immutable.from(initialState), action);
  assert.deepEqual(endState, expectedEndState);
});

test('With FETCH_NOTIFICATION_SETTINGS_FAILED, the notificationsStatus is properly set', function(assert) {
  const action = {
    type: ACTION_TYPES.FETCH_NOTIFICATION_SETTINGS_FAILED
  };
  const expectedEndState = {
    ...initialState,
    notificationsStatus: 'error'
  };

  const endState = reducer(Immutable.from(initialState), action);
  assert.deepEqual(endState, expectedEndState);
});

test('With TOGGLE_NOTIFICATION, the boolean property for the notification setting type gets flipped', function(assert) {
  const initState = {
    ...initialState,
    notificationSettings: [
      {
        reason: 'incident-created',
        sendToAssignee: true,
        sendToSocManagers: false // starts as false
      },
      {
        reason: 'incident-state-changed',
        sendToAssignee: false,
        sendToSocManagers: true
      }
    ]
  };
  const expectedEndState = {
    ...initialState,
    notificationSettings: [
      {
        reason: 'incident-created',
        sendToAssignee: true,
        sendToSocManagers: true // ends as true
      },
      {
        reason: 'incident-state-changed',
        sendToAssignee: false,
        sendToSocManagers: true
      }
    ]
  };

  const endState = reducer(Immutable.from(initState), {
    type: ACTION_TYPES.TOGGLE_NOTIFICATION,
    payload: {
      reason: 'incident-created',
      property: 'sendToSocManagers'
    }
  });
  assert.deepEqual(endState, expectedEndState);
});

test('With ADD_SOC_MANAGER_EMAIL, the payload email address is added', function(assert) {
  const expectedEndState = {
    ...initialState,
    socManagers: ['admins@rsa.com']
  };

  const endState = reducer(Immutable.from(initialState), {
    type: ACTION_TYPES.ADD_SOC_MANAGER_EMAIL,
    payload: 'admins@rsa.com'
  });
  assert.deepEqual(endState, expectedEndState);
});

test('With ADD_SOC_MANAGER_EMAIL, if the email already exists it is not added again', function(assert) {
  const initState = {
    ...initialState,
    socManagers: ['admins@rsa.com']
  };
  const expectedEndState = {
    ...initialState,
    socManagers: ['admins@rsa.com']
  };

  const endState = reducer(Immutable.from(initState), {
    type: ACTION_TYPES.ADD_SOC_MANAGER_EMAIL,
    payload: 'admins@rsa.com'
  });
  assert.deepEqual(endState, expectedEndState);
});

test('With REMOVE_SOC_MANAGER_EMAIL, the email address is removed', function(assert) {
  const initState = {
    ...initialState,
    socManagers: ['admins@rsa.com']
  };
  const expectedEndState = {
    ...initialState,
    socManagers: []
  };

  const endState = reducer(Immutable.from(initState), {
    type: ACTION_TYPES.REMOVE_SOC_MANAGER_EMAIL,
    payload: 'admins@rsa.com'
  });
  assert.deepEqual(endState, expectedEndState);
});

test('With SET_NOTIFICATION_EMAIL_SERVER, the selectedEmailServer is updated', function(assert) {
  const action = {
    type: ACTION_TYPES.SET_NOTIFICATION_EMAIL_SERVER,
    payload: 'my-test-server'
  };
  const expectedEndState = {
    ...initialState,
    selectedEmailServer: 'my-test-server'
  };

  const endState = reducer(Immutable.from(initialState), action);
  assert.deepEqual(endState, expectedEndState);
});


test('With UPDATE_NOTIFICATION_SETTINGS_STARTED, the state is properly set', function(assert) {
  const expectedEndState = {
    ...initialState,
    isTransactionUnderway: true
  };

  const endState = reducer(Immutable.from(initialState), {
    type: ACTION_TYPES.UPDATE_NOTIFICATION_SETTINGS_STARTED
  });
  assert.deepEqual(endState, expectedEndState);
});

test('With UPDATE_NOTIFICATION_SETTINGS, the state is properly updated', function(assert) {
  const action = {
    type: ACTION_TYPES.UPDATE_NOTIFICATION_SETTINGS
  };
  const initState = {
    ...initialState,
    isTransactionUnderway: true
  };
  const expectedEndState = {
    ...initialState,
    isTransactionUnderway: false
  };

  const endState = reducer(Immutable.from(initState), action);
  assert.deepEqual(endState, expectedEndState);
});

test('With UPDATE_NOTIFICATION_SETTINGS_FAILED, the state is properly updated', function(assert) {
  const action = {
    type: ACTION_TYPES.UPDATE_NOTIFICATION_SETTINGS
  };
  const initState = {
    ...initialState,
    isTransactionUnderway: true
  };
  const expectedEndState = {
    ...initialState,
    isTransactionUnderway: false
  };

  const endState = reducer(Immutable.from(initState), action);
  assert.deepEqual(endState, expectedEndState);
});