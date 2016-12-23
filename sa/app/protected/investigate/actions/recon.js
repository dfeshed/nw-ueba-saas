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
     * @param {object} item The Core event object to be reconstructed in the Recon UI.
     * @param {number} index The index of the item relative to the entire result set.
     * @public
     */
    reconOpen(endpointId, item, index) {
      const total = this.get('state.queryNode.value.results.eventCount.data');
      if (item && item.metas) {
        item.metas = [
          ['sessionId', item.sessionId],
          ['time', item.time],
          ...item.metas
        ];
      }
      this.get('state.recon').setProperties({
        isOpen: true,
        item,
        endpointId,
        metaPanelSizeWas: this.get('state.meta.panelSize'),
        index,
        total
      });
      this.send('metaPanelSize', 'min');
      this.send('contextPanelClose');
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
        isExpanded: false,
        item: undefined,
        endpointId: undefined
      });
      if (restoreMetaPanelSize) {
        this.send('metaPanelSize', this.get('state.recon.metaPanelSizeWas'));
      }
    },

    /**
     * Updates the UI state in order to expand the Recon UI.
     * @public
     */
    reconExpand() {
      this.set('state.recon.isExpanded', true);
    },

    /**
     * Updates the UI state in order to shrink (not close) the Recon UI.
     * @public
     */
    reconShrink() {
      this.set('state.recon.isExpanded', false);
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
          [ `${serviceId}/${start}/${end}/${query}` ]
        );
        window.open(url, '_blank');
      }
    }
  }
});
