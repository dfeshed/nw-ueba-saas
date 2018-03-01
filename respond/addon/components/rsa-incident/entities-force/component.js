import FastForce from 'respond/components/rsa-fast-force/component';
import HighlightsEntities from 'context/mixins/highlights-entities';
import { next } from '@ember/runloop';

/**
 * @class Entities Force Component
 * The same Component as Fast Force, but equipped with the HighlightsEntities Mixin from the context addon,
 * which enables the component to decorate nodes which correspond to entities (IPs, Domains, Users, Hosts, etc) and
 * to wire those nodes up to the context tooltip component.
 * @public
 */
export default FastForce.extend(HighlightsEntities, {

  // Configuration for wiring up entities to context lookups.
  // @see context/mixins/highlights-entities
  entitySelector: '.rsa-force-layout-node .text',
  entityEndpointId: 'IM',

  /**
   * Extends the inherited `dataJoin` by setting HTML attributes on entity DOM nodes. These HTML attributes are
   * required by the `highlights-entities` mixin in order for the mixin to find & highlight those DOM nodes.
   * @private
   */
  dataJoin() {
    // Update the DOM with the latest data.
    const joined = this._super();

    const { nodes } = joined || {};
    if (nodes) {

      // Set the HTML attributes required by HighlightsEntities mixin.
      nodes.select('.text').each(function({ type, value }) {
        this.setAttribute('data-entity-type', String(type).toUpperCase());
        this.setAttribute('data-entity-id', value);
      });

      // Now that DOM is updated, give the Component some breathing room for a moment (to start its usual d3
      // force-layout animation), and then kick off the highlighting of entities.
      if (this.get('autoHighlightEntities')) {
        next(this, 'highlightEntities');
      }
    }

    return joined;
  }
});
