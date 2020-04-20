import * as ACTION_TYPES from 'configure/actions/types/respond';
import {
  getSelectedEmailServerId,
  getNotificationSettings as getSettings,
  getSocManagerEmailAddresses
} from 'configure/reducers/respond/notifications/selectors';

const addSOCManagerEmail = (email) => {
  return {
    type: ACTION_TYPES.ADD_SOC_MANAGER_EMAIL,
    payload: email
  };
};

/**
 * Returns the notification settings for respond
 * @method getNotificationSettings
 * @public
 */
const getNotificationSettings = () => {
  return {
    type: ACTION_TYPES.FETCH_NOTIFICATION_SETTINGS_SAGA
  };
};

const removeSOCManagerEmail = (email) => {
  return {
    type: ACTION_TYPES.REMOVE_SOC_MANAGER_EMAIL,
    payload: email
  };
};

const setSelectedEmailServer = (selectedEmailServerId) => {
  return {
    type: ACTION_TYPES.SET_NOTIFICATION_EMAIL_SERVER,
    payload: selectedEmailServerId
  };
};

const toggleNotification = (reason, property) => {
  return {
    type: ACTION_TYPES.TOGGLE_NOTIFICATION,
    payload: { reason, property }
  };
};

const updateNotificationSettings = () => {
  return (dispatch, getState) => {
    const state = getState();
    const notificationSettings = {
      selectedEmailServer: getSelectedEmailServerId(state),
      socManagers: getSocManagerEmailAddresses(state),
      notificationSettings: getSettings(state)
    };
    dispatch({
      type: ACTION_TYPES.UPDATE_NOTIFICATION_SETTINGS_SAGA,
      notificationSettings
    });
  };
};

export {
  addSOCManagerEmail,
  getNotificationSettings,
  removeSOCManagerEmail,
  setSelectedEmailServer,
  toggleNotification,
  updateNotificationSettings
};