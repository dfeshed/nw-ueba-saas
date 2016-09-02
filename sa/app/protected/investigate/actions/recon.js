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
     * @public
     */
    reconOpen(endpointId, item) {
      this.get('state.recon').setProperties({
        isOpen: true,
        item,
        endpointId,
        metaPanelSizeWas: this.get('state.meta.panelSize')
      });
      this.send('metaPanelSize', 'min');
    },

    /**
     * Updates the UI state in order to hide the Recon UI.
     * @public
     */
    reconClose(restoreMetaPanelSize = false) {
      this.get('state.recon').setProperties({
        isOpen: false,
        item: undefined,
        endpointId: undefined
      });
      if (restoreMetaPanelSize) {
        this.send('metaPanelSize', this.get('state.recon.metaPanelSizeWas'));
      }
    }
  }
});
