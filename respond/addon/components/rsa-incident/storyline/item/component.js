import Ember from 'ember';
import ListItem from 'respond/components/rsa-list/item/component';
import layout from './template';
import computed from 'ember-computed-decorators';
import EnrichmentsToDisplay from './enrichments-to-display';

const { isEmpty } = Ember;

/**
 * @class Storyline Item component
 * A subclass of List Item component which renders data from a storypoint object, including the summary information
 * about the storypoint's corresponding indicator, and the enrichments in the indicator's events (if any).
 * @public
 */
export default ListItem.extend({
  tagName: 'vbox',
  classNames: ['rsa-incident-storyline-item'],
  classNameBindings: ['item.indicator.isCatalyst:is-catalyst', 'item.isHidden:is-hidden'],
  layout,

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
  enrichmentThreshold: 50,

  /**
   * Configurable ordered list of enrichments to be displayed in DOM (in display order).
   *
   * Each member of this array is an enrichment key that should be displayed in this component's DOM,
   * if that enrichment meets all of the following criteria:
   * (i) the enrichment is defined for this component;
   * (ii) the enrichment score is >= some minimum threshold.
   *
   * @type {string[]}
   * @private
   */
  enrichmentKeys: EnrichmentsToDisplay,

  /**
   * Given the current `item`, computes an array (possibly empty) of `item`'s enrichment scores  in the order specified
   * by `enrichmentKeys`, omitting any enrichments whose score is less than `enrichemntThreshold`.
   *
   * Each array item will be an object with the following properties:
   * `key` (string): the enrichment identifier;
   * `score` (number): the score for that enrichment.
   *
   * @type {{ key: string, score: number }[]}
   * @public
   */
  @computed('item.indicator.enrichments', 'enrichmentThreshold', 'enrichmentKeys')
  enrichments(hash, threshold, keys) {
    if (!hash) {
      return [];
    } else {
      return keys
        .map((key) => ({ key, score: hash[key] || 0 }))
        .filter((obj) => (obj.score > threshold));
    }
  },

  @computed('item.matched')
  resolvedMatched(matched = []) {
    return matched.reject(isEmpty);
  }
});
