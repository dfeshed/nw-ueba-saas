import Service from 'ember-service';
import service from 'ember-service/inject';
import { getSocketDetails } from 'preferences/utils/preference-socket-provider';


/**
 * @class Preferences service
 * A global API for fetching/setting preferences for logged in user for given service.
 *   (e.g., For investigate, default view type, default download type ).
 * @public
 */
export default Service.extend({
  request: service(),
  /**
   * Returns a promise for the default preferences settings.
   *
   * @param {String} [preferenceFor='events'] Can pass different preferenceName,
   * @example
   * ```js
   * events, respond, navigate
   * ```
   * @returns {Promise}
   * @public
   */
  getPreferences(preferenceFor) {
    const requestPayload = getSocketDetails(preferenceFor, 'get');
    requestPayload.query = {
      filter: [
        { field: 'preferenceFor', value: preferenceFor }
      ]
    };
    return this.get('request').promiseRequest(requestPayload);
  },

  /**
   * Returns a promise after saving preferences settings.
   *
   * @param {String} [preferenceFor='events'] Can pass different preferenceName,
   * @param {Object} [preferenceObject] Need to pass JSON object to save preferences,
   * @example
   * ```js
   * {
       preferenceFor: 'events'
       eventsPreferences: {
          defaultAnalysisView: 'text'
       }
     }
   * ```
   * @returns {Promise}
   * @public
   */
  setPreferences(preferenceFor, preferences) {

    const requestPayload = getSocketDetails(preferenceFor, 'set');
    requestPayload.query = {
      filter: [
        { field: 'investigatePreferences', value: preferences }
      ]
    };
    return this.get('request').promiseRequest(requestPayload);
  }
});
