/**
 * @file Investigate Route Recon Actions
 * Route actions related to opening/closing/interacting with the Recon UI.
 * These actions assume that the state is accessible via `this.get('state')`.
 * @public
 */
import Ember from 'ember';

const { inject, Mixin } = Ember;

export default Mixin.create({

  _routing: inject.service('-routing'),

  actions: {
    /**
     * Updates state in order to reveal the Recon UI and feed it a server event record.
     * @param {string} endpointId The Core service ID from which the event came from.
     * @param {string} eventId The ID of the Core event object to be reconstructed in the Recon UI.
     * @param {meta[]} metas The array of metas for the event
     * @param {number} index The index of the item relative to the entire result set.
     * @public
     */
    reconOpen(endpointId, eventId, metas, index) {
      const total = this.get('state.queryNode.value.results.eventCount.data');

      this.get('state.recon').setProperties({
        isOpen: true,
        endpointId,
        eventId,
        metaPanelSizeWas: this.get('state.meta.panelSize'),
        metas,
        index,
        total
      });

      this.send('metaPanelSize', 'min');
      this.send('contextPanelClose');

      const isExpanded = this.get('state.recon.isExpanded');
      this.transitionTo({ queryParams: { eventId, reconSize: isExpanded ? 'max' : 'min' } });
    },

    /**
     * Updates the UI state in order to hide the Recon UI.
     * @public
     */
    reconClose(restoreMetaPanelSize = false) {
      if (!this.get('state.recon.isOpen')) {
        // Recon UI isn't open. Don't make any state changes, just exit.
        return;
      }
      this.get('state.recon').setProperties({
        isOpen: false,
        endpointId: undefined,
        eventId: undefined,
        metas: undefined
      });
      if (restoreMetaPanelSize) {
        this.send('metaPanelSize', this.get('state.recon.metaPanelSizeWas'));
      }
      this.transitionTo({ queryParams: { eventId: -1, metaPanelSize: 'default', reconSize: 'max' } });
    },

    /**
     * Updates the UI state in order to expand the Recon UI.
     * @public
     */
    reconSizeReceived(reconSize) {
      if (!reconSize) {
        return;
      }

      const currentlyExpanded = this.get('state.recon.isExpanded');

      if (reconSize === 'min' && currentlyExpanded) {
        this.send('reconShrink');
      }

      if (reconSize === 'max' && !currentlyExpanded) {
        this.send('reconExpand');
      }
    },

    /**
     * Updates the UI state in order to expand the Recon UI.
     * @public
     */
    reconExpand() {
      this.set('state.recon.isExpanded', true);
      this.transitionTo({ queryParams: { reconSize: 'max' } });
    },

    /**
     * Updates the UI state in order to shrink (not close) the Recon UI.
     * @public
     */
    reconShrink() {
      this.set('state.recon.isExpanded', false);
      this.transitionTo({ queryParams: { reconSize: 'min' } });
    },

    /**
     * Opens a query, in another tab, for the events associated with a given file.
     * Used for drilling into the contents of a "linked" file discovered in Recon.
     * @param {Object} file The file object.
     * @param {String} file.query The Core service query string to be used for drilling.
     * @param {Number} file.start The start date to be used with the Core query.
     * @param {Number} file.end The end date to be used with the Core query.
     * @public
     */
    reconLinkToFile(file = {}) {
      const { start, end } = file;
      let { query = '' } = file;

      // Remove surrounding quotes from query, if any
      const hasSurroundingQuotes = query.match(/^"(.*)"$/);
      if (hasSurroundingQuotes) {
        query = hasSurroundingQuotes[1];
      }

      if (query && start && end) {
        const serviceId = this.get('state.queryNode.value.definition.serviceId');
        const routing = this.get('_routing');
        const url = routing.generateURL(
          routing.get('currentRouteName'),
          [`${serviceId}/${start}/${end}/${query}`]
        );
        window.open(url, '_blank');
      }
    }
  }
});
