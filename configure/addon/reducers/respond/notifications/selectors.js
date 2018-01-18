import reselect from 'reselect';
import _ from 'lodash';

const { createSelector } = reselect;

const notificationsState = (state) => state.configure.respond.notifications;

export const getNotificationsStatus = createSelector(
  notificationsState,
  (notificationsState) => notificationsState.notificationsStatus
);

export const getEmailServers = createSelector(
  notificationsState,
  (notificationsState) => notificationsState.emailServers
);

export const getEnabledEmailServers = createSelector(
  getEmailServers,
  (emailServers) => emailServers.filterBy('enabled', true)
);

export const getSelectedEmailServerId = createSelector(
  notificationsState,
  (notificationsState) => notificationsState.selectedEmailServer
);

export const getSelectedEmailServer = createSelector(
  getEnabledEmailServers, getSelectedEmailServerId,
  (enabledEmailServers, selectedEmailServer) => {
    return enabledEmailServers.findBy('id', selectedEmailServer);
  }
);

export const getNotificationSettings = createSelector(
  notificationsState,
  (notificationsState) => notificationsState.notificationSettings
);

export const getSocManagerEmailAddresses = createSelector(
  notificationsState,
  (notificationsState) => notificationsState.socManagers
);

export const isTransactionUnderway = createSelector(
 notificationsState,
  (notificationsState) => notificationsState.isTransactionUnderway
);

export const hasSocManagerEmails = createSelector(
  getSocManagerEmailAddresses,
  (socManagerEmails) => socManagerEmails.length > 0
);

// The email server ID must be set by the user
export const isMissingRequiredData = createSelector(
  getSelectedEmailServerId,
  (selectedEmailServerId) => !_.isString(selectedEmailServerId)
);

export const getOriginalSettings = createSelector(
  notificationsState,
  (notificationsState) => notificationsState.originalSettings
);

export const hasUnsavedChanges = createSelector(
  [getSelectedEmailServerId, getSocManagerEmailAddresses, getNotificationSettings, getOriginalSettings],
  (selectedEmailServer, socManagers, notificationSettings, originalSettings) => {
    const currentSettings = { selectedEmailServer, socManagers, notificationSettings };
    return !_.isEqual(currentSettings, originalSettings);
  }
);