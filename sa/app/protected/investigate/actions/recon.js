/**
 * @file Investigate Route Recon Actions
 * Route actions related to opening/closing/interacting with the Recon UI.
 * These actions assume that the state is accessible via `this.get('state')`.
 * @public
 */
import Ember from 'ember';

const { Mixin } = Ember;

export default Mixin.create({
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
    }

  }
});
