import Service from 'ember-service';
import service from 'ember-service/inject';
import rsvp from 'rsvp';
import Immutable from 'seamless-immutable';

/**
 * This object will cache preferences on module basis. So next time user is requesting for any
 * module preferences then same will be returned using this cached object.
 *
 * This will help to avoid multiple server calls.
 *   (e.g., For investigate, default view type, default download type ).
 * @private
 */
let preferencesObj = Immutable.from({ });

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
   * @param {String} [preferenceFor='investigate-events'] Can pass different preferenceName,
   * @example
   * ```js
   * events, respond, navigate
   * ```
   * @returns {Promise}
   * @public
   */
  getPreferences(preferenceFor) {
    const modulePref = preferencesObj.getIn([preferenceFor]);
    if (modulePref) {
      return new rsvp.Promise(function(resolve) {
        resolve(modulePref);
      });
    }
    const requestPayload = {
      modelName: `${preferenceFor}-preferences`,
      method: 'getPreferences',
      query: {
        data: preferenceFor
      }
    };
    const promiseObj = this.get('request').promiseRequest(requestPayload);
    promiseObj.then((response) => {
      preferencesObj = preferencesObj.set(preferenceFor, response);
    });
    return promiseObj;
  },

  /**
   * Returns a promise after saving preferences settings.
   *
   * @param {String} [preferenceFor='investigate-events'] Can pass different preferenceName,
   * @param {Object} [preferenceObject] Need to pass JSON object to save preferences,
   * @example
   * ```js
    {
      eventsPreferences: {
        defaultAnalysisView: 'text'
      }
    }
   * ```
   * @returns {Promise}
   * @public
   */
  setPreferences(preferenceFor, preferences) {
    const requestPayload = {
      modelName: `${preferenceFor}-preferences`,
      method: 'setPreferences',
      query: {
        data: preferences
      }
    };
    const promiseObj = this.get('request').promiseRequest(requestPayload);
    promiseObj.then(() => {
      const prefObj = preferencesObj.getIn([preferenceFor]);
      preferencesObj = preferencesObj.set(preferenceFor, prefObj ? prefObj.merge(preferences) : preferences);
    });
    return promiseObj;
  }
});
