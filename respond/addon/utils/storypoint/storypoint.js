import EmberObject from 'ember-object';
import EnrichmentsToDisplay from './enrichments-to-display';
import KeysToi18n from './keys-to-i18n';
import computed, { alias } from 'ember-computed-decorators';
import { isEmpty } from 'ember-utils';
import get from 'ember-metal/get';

/**
 * @class StoryPoint
 * A wrapper object for indicators (alerts) that are to be displayed in a "storyline"-like UI.
 *
 * Such indicators will need a list of events, which must be fetched from a separate API and then stored in this
 * object with the indicator definition.
 *
 * The storyline UI will actually be implemented as an `rsa-group-table`, therefore this data structure is designed
 * to mimic a `Group` object that can be consumed by a group table (@see respond/mixins/group-table/has-grouped-rows).
 * Specifically, each group is supposed to have an `items` array, which is expanded/collapsed based on the group's
 * `isOpen` boolean.  In this storyline UI, the user can toggle each group to view either the events or the enrichments.
 * Therefore each group has a boolean `showEnrichmentsAsItem`; if falsey, the events are used for the items;
 * otherwise, the enrichments are used for the items.
 *
 * @public
 */
export default EmberObject.extend({

  /**
   * The indicator POJO that this instance wraps.
   * @type {Object}
   * @public
   */
  indicator: null,

  @alias('indicator.id')
  id: null,

  /**
   * The list of events for this indicator.
   * @type {Object[]}
   * @public
   */
  events: null,

  // Indicates whether or not the group's items should be shown or collapsed in the UI.
  // @see rsa-group-table
  isOpen: true,

  /**
   * Indicates whether the group's items should be read from the alert events (false) or event enrichments (true).
   * @type {Boolean}
   * @default false
   * @public
   */
  showEnrichmentsAsItems: false,

  /**
   * The contents of the property path currently specified by `itemsAttr`.
   * @type {Object[]}
   * @public
   */
  @computed('showEnrichmentsAsItems', 'enrichments', 'events')
  items(showEnrichmentsAsItems, enrichments, events) {
    return showEnrichmentsAsItems ? enrichments : events;
  },

  /**
   * Computes an array of enrichments (possibly empty) from the current list of events.
   *
   * Enrichments are actually event properties. Each event may have an (optional) `enrichment` POJO, which is a
   * deeply nested hash of various enrichment key-value pairs, grouped by "activity" (as ESA likes to call them).
   * There are several supported "activities", which can each be enabled/disabled/configured separately.  Each activity
   * will have its own set of enrichment data, which is basically key-value pairs.
   * Here we aggregate the enrichments into a flat array that can be more easily consumed by the UI.
   *
   * Some/most of these key-value pairs are not intended for display; they are more like private variables used
   * to compute some quantity for user display (usually some sort of "score").  So our computed result here will
   * not include all the enrichments; just the ones meant for display.
   *
   * @example
   * ```
   * enrichment: {
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
   * @type {{ id: String, key: String, i18nKey: String, value: * }[]}
   * @public
   */
  @computed('events.lastObject')
  enrichments(evt) {
    const { id: eventId, enrichment: enrichmentHash = {} } = evt || {};

    // Build an array of enrichment keys & values that are intended for UI display.
    return EnrichmentsToDisplay
      .map(({ dataKey, threshold }) => {
        const dataValue = get(enrichmentHash, dataKey || '');

        // Filter out the keys whose values don't meet their corresponding thresholds (if any).
        if (isEmpty(dataValue)) {
          return null;    // enrichment value is not specified; skip it
        }
        if (threshold && (dataValue < threshold)) {
          return null;    // enrichment value doesn't meet threshold; skip it
        }

        // Compute the i18n string key that should be used to render this enrichment.
        // For 'domain_is_whitelisted', if the value is `false`, use the i18n key for 'domain_is_not_whitelisted'.
        let i18nKey = dataKey;
        if ((String(dataKey).indexOf('domain_is_whitelisted') > -1) && !dataValue) {
          i18nKey = dataKey.replace('domain_is_whitelisted', 'domain_is_not_whitelisted');
        }

        return {
          isEnrichment: true,
          id: eventId,
          key: dataKey,
          i18nKey: KeysToi18n[i18nKey],
          value: dataValue,
          allEnrichments: enrichmentHash
        };
      })
      .compact();
  }
});