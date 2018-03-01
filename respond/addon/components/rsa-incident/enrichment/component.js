import Component from '@ember/component';
import { get } from '@ember/object';
import { isEmpty } from '@ember/utils';
import computed from 'ember-computed-decorators';
import KeysToi18n from './keys-to-i18n';

/**
 * @class Incident Enrichments component
 * Displays a single entry from a set of enrichment data.
 *
 * This component encapsulates the intelligence for parsing an enrichment into a user-friendly presentation.
 * Generally, it's pretty straightforward: the enrichment score is tested against some minimum threshold, and
 * if it passes the threshold, it is mapped to i18n text and rendered in DOM.  There are some minor exceptions,
 * however, and they are handled by this component as well.
 *
 * Note that enrichment data structures can & will evolve over releases, so this component will likely
 * evolve & iterate over time.
 * @public
 */
export default Component.extend({
  tagName: '',

  /**
   * The enrichment hash from which data is to be looked up. Possibly null or empty.
   *
   * The enrichment data structure will evolve over time. As of this writing, the overall structure is a hierarchy
   * of nested POJOs.  The general hierarchy is, in backend terminology, "activity" -> "enrichments".  There are
   * several supported "activities", which can each be enabled/disabled/configured separately.  Each activity
   * will have its own set of "enrichments", which are basically key-value pairs.
   *
   * Some/most of these key-value pairs are not intended for display; they are more like private variables used
   * to compute some quantity for user display (usually some sort of "score").
   *
   * Bottom line: the backend spec is kinda quirky, not well-documented & subject to change. For now, whenever the
   * backend changes this structure, we'll need to update this component.
   *
   * @example
   * ```
   * {
   *   ctxhub: {
   *     'domain_is_whitelisted': false
   *   },
   *   domain: {
   *     'ua_ratio_score': 100,
   *     'ua_score': 100,
   *     'referer_score': 100,
   *     ..
   *   },
   *   whois: {
   *     'age_score': 100,
   *     'validity_score': 100,
   *     ..
   *   },
   *   smooth: {
   *     'smooth_beaconing_score': 100
   *   }
   * }
   * ```
   *
   * @type {object}
   * @public
   */
  data: null,

  /**
   * The lookup key for `data` with which information will be read and displayed.
   *
   * @type {string}
   * @public
   */
  dataKey: null,

  /**
   * Configurable minimum enrichment score required in order to display an enrichment in this component's DOM.
   *
   * Enrichments are essentially extra properties that the backend adds to the data objects.  Each enrichment typically
   * is just an identifier (a "key") and a score.  The key might specify some sort of contextual information; e.g.
   * one key may specific that the domain in question will be expiring soon.  The score indicates a level of
   * confidence about that key, between 0 - 100.  If the score is low, the confidence is low and the enrichment could
   * be incorrect.  So it is useful to set some minimum threshold under which we don't bother showing an enrichment.
   *
   * @type {number}
   * @private
   */
  threshold: 50,

  /**
   * Computes the i18n key for the display text that corresponds to `dataKey`.
   * @type {string}
   * @private
   */
  @computed('dataKey', 'data')
  i18nKey(dataKey, data) {
    if (String(dataKey).indexOf('domain_is_whitelisted') > -1) {

      // For 'domain_is_whitelisted', if the value is `false`, use the i18n key for 'domain_is_not_whitelisted'.
      const value = get(data || {}, dataKey);
      if (!value) {
        dataKey = dataKey.replace('domain_is_whitelisted', 'domain_is_not_whitelisted');
      }
    }
    return KeysToi18n[dataKey];
  },

  /**
   * Computes whether or not the score for the given `dataKey` in `data` meets the given `threshold` minimum.
   * If threshold is not specified, returns `true`.
   * @type {boolean}
   * @private
   */
  @computed('dataKey', 'data', 'threshold')
  shouldDisplay(dataKey, data, threshold) {
    data = data || {};
    const dataValue = get(data, dataKey || '');
    if (isEmpty(dataValue)) {
      return false;
    }
    return !threshold ? true : dataValue > threshold;
  }
});