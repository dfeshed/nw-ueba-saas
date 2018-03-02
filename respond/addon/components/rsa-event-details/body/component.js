import Component from '@ember/component';
import layout from './template';
import HighlightsEntities from 'context/mixins/highlights-entities';
import { inject as service } from '@ember/service';
import { next } from '@ember/runloop';
import computed from 'ember-computed-decorators';
import { underscore, capitalize } from '@ember/string';
import { get } from '@ember/object';

// Checks if a given i18n lookup result is "empty". This is done by looking for the special text "Missing translation".
function is18nValueMissing(safeStr) {
  return !safeStr || (safeStr.toString().indexOf('Missing translation') > -1);
}

// Takes a property name with words separated by an underscore or in camelcase, and converts it to
// a string of space-delimited Capitalized Words.
function capitalizeWords(str) {
  return underscore(str).split('_')
    .map(capitalize)
    .join(' ');
}

/**
 * @class Event Details Body component
 * Renders the property values of a normalized alert event.
 * @public
 */
export default Component.extend(HighlightsEntities, {
  tagName: 'section',
  layout,
  classNames: ['rsa-event-details-body'],
  i18n: service(),

  /**
   * The normalized alert event POJO.
   * @type {object}
   * @public
   */
  model: null,

  // Shortcut to the c2 POJO, which may be in 1 of 2 paths under the enrichment POJO.
  @computed('model')
  c2data: (model) => {
    if (!model) {
      return null;
    }
    return get(model, 'enrichment.http-packet.c2') || get(model, 'enrichment.http-log.c2');
  },

  // Configuration for wiring up entities to context tooltip.
  // @see context/addon/mixins/highlights-entities
  autoHighlightEntities: true,
  entityEndpointId: 'IM',

  // Re-triggers the highlighting of entities whenever model changes.
  didReceiveAttrs() {
    const model = this.get('model');
    if (model !== this._lastModel) {
      this._lastModel = model;
      if (model) {
        next(this, 'highlightEntities');
      }
    }
  },

  // Formatter to convert normalized event's property names into user-friendly display names.
  @computed
  propertyNameFormatter() {
    const i18n = this.get('i18n');
    const prefix = 'respond.eventDetails.labels.';

    return (name, fullPath) => {

      // First remove the C2 flow name (if present), since it is irrelevant.
      name = name.replace('http-packet_', '').replace('http-log_', '');

      // Next look for an i18n entry for the full path.
      // If no entry found for full path, lookup one for just the leaf property name.
      const result = i18n.t(
        `${prefix}${fullPath.replace(/\./g, '_')}`,
        { default: `${prefix}${name}` }
      );

      // When all else fails, capitalize the property name (without any language translation).
      if (is18nValueMissing(result)) {
        return capitalizeWords(name);
      }
      return result;
    };
  }
});
