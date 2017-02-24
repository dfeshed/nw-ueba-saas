import Ember from 'ember';
import EnrichmentKeys from 'respond/utils/enrichment/keys';

const {
  Helper,
  inject: { service }
} = Ember;

// Generates a description string for a given enrichment key; i.e. a localized text string that describes
// the enrichment in a user-friendly sentence (and possibly includes the score value too).
export function enrichmentDesc(i18n, key, score) {
  const labelKey = EnrichmentKeys[key];
  if (labelKey) {
    const niceScore = Math.round(score);
    return i18n.t(labelKey, { score: niceScore });
  } else {
    return '';
  }
}

/**
 * Generates a localized description string for a given enrichment key & score.
 *
 * Unlike the typical stateless Ember Helpers, this is a class-based Helper because it needs to access the i18n service.
 *
 * @param {string} key The enrichment key.
 * @param {number} score The enrichment score.
 * @public
 */
export default Helper.extend({
  i18n: service(),
  compute([key], { score }) {
    const i18n = this.get('i18n');
    return enrichmentDesc(i18n, key, score);
  }
});

