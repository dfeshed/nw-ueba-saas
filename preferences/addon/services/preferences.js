import Service from '@ember/service';
import { fetchPreferences, savePreferences } from 'preferences/actions/fetchPreferences';
import _ from 'lodash';
import { debug } from '@ember/debug';


/**
 * @class Preferences service
 * A global API for fetching/setting preferences for logged in user for given service.
 *   (e.g., For investigate, default view type, default download type ).
 * @public
 */
export default Service.extend({
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
  getPreferences(preferenceFor, additionalFilters) {
    return fetchPreferences(preferenceFor, additionalFilters);
  },

  /**
   * Returns a promise after saving preferences settings.
   *
   * @param {String} [preferenceFor='investigate-events'] Can pass different preferenceName,
   * @param {Object} [preferences] Need to pass JSON object to save preferences,
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
  setPreferences(preferenceFor, additionalFilters, preferences, defaultPreferences) {
    return fetchPreferences(preferenceFor, additionalFilters).then((data) => {
      const preferenceForSave = _.assign(data || defaultPreferences, preferences);
      return savePreferences(preferenceFor, preferenceForSave);
    }).catch(() => {
      // TODO:: Need to handle error gracefully.
      debug('Unable to save preferences.');
    });

  }
});
