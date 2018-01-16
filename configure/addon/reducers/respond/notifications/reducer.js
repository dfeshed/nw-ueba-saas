import Immutable from 'seamless-immutable';
import * as ACTION_TYPES from 'configure/actions/types/respond';
import reduxActions from 'redux-actions';

const initialState = {
  emailServers: [],
  selectedEmailServer: null,
  notificationsStatus: null, // wait, completed, error
  socManagers: [],
  notificationSettings: [],
  isTransactionUnderway: false
};

export default reduxActions.handleActions({
  [ACTION_TYPES.FETCH_NOTIFICATION_SETTINGS_STARTED]: (state) => {
    return state.merge({
      emailServers: [],
      selectedEmailServer: null,
      notificationsStatus: 'wait',
      socManagers: [],
      notificationSettings: []
    });
  },
  [ACTION_TYPES.FETCH_NOTIFICATION_SETTINGS]: (state, action) => {
    const { payload: { emailServers, selectedEmailServer, notificationSettings, socManagers } } = action;
    return state.merge({
      notificationsStatus: 'completed',
      emailServers,
      selectedEmailServer: selectedEmailServer || null,
      socManagers,
      notificationSettings
    });
  },
  [ACTION_TYPES.FETCH_NOTIFICATION_SETTINGS_FAILED]: (state) => {
    return state.merge({
      notificationsStatus: 'error'
    });
  },
  [ACTION_TYPES.TOGGLE_NOTIFICATION]: (state, { payload: { reason, property } }) => {
    const notificationSettings = state.notificationSettings.map((setting) => {
      return setting.reason === reason ? setting.set(property, !setting[property]) : setting;
    });
    return state.set('notificationSettings', notificationSettings);
  },
  [ACTION_TYPES.ADD_SOC_MANAGER_EMAIL]: (state, { payload }) => {
    // if the email is not already in the soc manager email list, add to the list
    return !state.socManagers.includes(payload) ?
      state.set('socManagers', [...state.socManagers, payload]) : state;
  },
  [ACTION_TYPES.REMOVE_SOC_MANAGER_EMAIL]: (state, { payload }) => {
    const socManagers = state.socManagers.without(payload);
    const notificationSettings = socManagers.length ? state.notificationSettings : state.notificationSettings.map((setting) => {
      return setting.sendToSocManagers ? setting.set('sendToSocManagers', false) : setting;
    });
    return state.merge({
      socManagers,
      notificationSettings
    });
  },
  [ACTION_TYPES.SET_NOTIFICATION_EMAIL_SERVER]: (state, { payload }) => {
    return state.set('selectedEmailServer', payload);
  },
  [ACTION_TYPES.UPDATE_NOTIFICATION_SETTINGS_STARTED]: (state) => {
    return state.merge({
      isTransactionUnderway: true
    });
  },
  [ACTION_TYPES.UPDATE_NOTIFICATION_SETTINGS]: (state) => {
    return state.merge({
      isTransactionUnderway: false
    });
  },
  [ACTION_TYPES.UPDATE_NOTIFICATION_SETTINGS_FAILED]: (state) => {
    return state.merge({
      isTransactionUnderway: false
    });
  }
}, Immutable.from(initialState));