/**
 * @private
 * @description This object describes socket configuration for all services.
 * @type object
 */
const PREFERENCES_SOCKET_DETAILS = {
  events: {
    get: {
      modelName: 'investigate-preferences',
      method: 'getPreferences'
    },
    set: {
      modelName: 'investigate-preferences',
      method: 'setPreferences'
    }
  }
};


/**
 * @public
 * @description Get socket details for given Preference name.
 *
 * @param {string} preferenceName Preferences name key to get socket details .
 *
 * @returns {object} websocket details.
 * @example
 ```js
 {
   modelName: 'investigate-preferences',
   method: 'eventsSocketUrl'
 }
 */
const getSocketDetails = (preferenceName, type) => {
  return PREFERENCES_SOCKET_DETAILS[preferenceName] ? PREFERENCES_SOCKET_DETAILS[preferenceName][type] : {};
};
export { getSocketDetails };
