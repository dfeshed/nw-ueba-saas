import Component from '@ember/component';
import { connect } from 'ember-redux';
import { inject } from '@ember/service';
import computed from 'ember-computed-decorators';
import {
  getEnabledEmailServers,
  getSocManagerEmailAddresses,
  getSelectedEmailServer,
  getNotificationSettings,
  getNotificationsStatus,
  isTransactionUnderway,
  hasSocManagerEmails,
  isMissingRequiredData,
  hasUnsavedChanges
} from 'configure/reducers/respond/notifications/selectors';

import {
  setSelectedEmailServer,
  toggleNotification,
  addSOCManagerEmail,
  removeSOCManagerEmail,
  updateNotificationSettings
} from 'configure/actions/creators/respond/notification-creators';

const stateToComputed = (state) => {
  return {
    isTransactionUnderway: isTransactionUnderway(state),
    notificationsStatus: getNotificationsStatus(state),
    emailServers: getEnabledEmailServers(state),
    selectedEmailServer: getSelectedEmailServer(state),
    socManagerEmailAddresses: getSocManagerEmailAddresses(state),
    hasSocManagerEmails: hasSocManagerEmails(state),
    notificationSettings: getNotificationSettings(state),
    isMissingRequiredData: isMissingRequiredData(state),
    hasUnsavedChanges: hasUnsavedChanges(state)
  };
};

const dispatchToActions = function(dispatch) {
  return {
    updateEmailServer: (server) => {
      dispatch(setSelectedEmailServer(server.id));
    },
    toggle: (reason, property) => {
      dispatch(toggleNotification(reason, property));
    },
    addSOCManagerEmail: (email) => {
      dispatch(addSOCManagerEmail(email));
    },
    removeSOCManagerEmail: (email) => {
      dispatch(removeSOCManagerEmail(email));
    },
    save: () => {
      dispatch(updateNotificationSettings());
    }
  };
};

const VALID_EMAIL_REGEX = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;

/**
 * @class RespondNotifications
 * @public
 */
const RespondNotifications = Component.extend({
  accessControl: inject(),
  tagName: 'vbox',
  classNames: ['notifications', 'flexi-fit'],
  classNameBindings: ['isTransactionUnderway:transaction-in-progress'],
  emailToAdd: null,
  actions: {
    addEmail() {
      const emailToAdd = this.get('emailToAdd');
      this.send('addSOCManagerEmail', emailToAdd);
      this.set('emailToAdd', null); // reset the email to empty
    }
  },
  @computed('emailToAdd')
  isEmailInvalid(emailToAdd) {
    return !VALID_EMAIL_REGEX.test(emailToAdd);
  },
  // determines whether the Apply button should be unavailable. That is, if the user has no permissions,
  // the user is missing required information in the form, or the user has no changes that need to be saved.
  @computed('accessControl.respondCanManageNotifications', 'isMissingRequiredData', 'hasUnsavedChanges')
  isApplyUnavailable(hasManagePermissions, isMissingData, hasUnsavedChanges) {
    return !hasManagePermissions || isMissingData || !hasUnsavedChanges;
  }
});

export default connect(stateToComputed, dispatchToActions)(RespondNotifications);