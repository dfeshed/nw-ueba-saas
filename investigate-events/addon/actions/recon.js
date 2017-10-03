/**
 * @file Investigate Route Recon Actions
 * Route actions related to opening/closing/interacting with the Recon UI.
 * These actions assume that the state is accessible via `this.get('state')`.
 * @public
 */
import Mixin from 'ember-metal/mixin';
import service from 'ember-service/inject';
import {
  setMetaPanelSize
} from 'investigate-events/actions/interaction-creators';

export default Mixin.create({
  _routing: service('-routing'),
  redux: service(),

  actions: {
    /**
     * Updates state in order to reveal the Recon UI and feed it a server event record.
     * @param {string} serviceId The Core service ID from which the event came from.
     * @param {string} eventId The ID of the Core event object to be reconstructed in the Recon UI.
     * @param {meta[]} metas The array of metas for the event
     * @param {number} index The index of the item relative to the entire result set.
     * @public
     */
    reconOpen(serviceId, eventId, metas, index) {
      const total = this.get('state.queryNode.value.results.eventCount.data');
      const { metaPanelSize, reconPanelSize } = this.get('redux').getState();

      this.get('state.recon').setProperties({
        isOpen: true,
        serviceId,
        eventId,
        metaPanelSizeWas: metaPanelSize,
        metas,
        index,
        total
      });

      this.get('redux').dispatch(setMetaPanelSize('min'));
      this.send('contextPanelClose');

      this.transitionTo({ queryParams: { eventId, reconPanelSize } });
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
        serviceId: undefined,
        eventId: undefined,
        metas: undefined
      });
      // If restoreMetaPanelSize, use the old one, otherwise use 'default'
      const metaPanelSize = restoreMetaPanelSize ? this.get('state.recon.metaPanelSizeWas') : 'default';
      this.transitionTo({ queryParams: { eventId: -1, metaPanelSize } });
    },

    /**
     * Updates the UI state in order to expand the Recon UI.
     * @public
     */
    reconExpand() {
      // this.set('state.recon.size', 'max');
      this.transitionTo({ queryParams: { reconSize: 'max' } });
    },

    /**
     * Updates the UI state in order to shrink (not close) the Recon UI.
     * @public
     */
    reconShrink() {
      // this.set('state.recon.size', 'min');
      this.transitionTo({ queryParams: { reconSize: 'min' } });
    },

    reconFull() {
      // this.set('state.recon.size', 'full');
      this.transitionTo({ queryParams: { reconSize: 'full' } });
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
          [`${serviceId}/${start}/${end}/${query}`],
          { eventId: -1 }
        );
        window.open(url, '_blank');
      }
    },

    toggleReconSize() {
      if (this.get('state.recon.isOpen')) {
        const { reconPanelSize: size } = this.get('redux').getState();
        if (size === 'max') {
          this.send('reconShrink');
        } else if (size === 'min') {
          this.send('reconExpand');
        }
      }
    },

    toggleSlaveFullScreen() {
      if (this.get('state.recon.isOpen')) {
        const { reconPanelSize: size } = this.get('redux').getState();
        if (size === 'full') {
          // Set to previous size
          if (this.get('_size') === 'min') {
            this.send('reconShrink');
          } else {
            this.send('reconExpand');
          }
        } else {
          // save off previous size
          this.set('_size', size);
          this.send('reconFull');
        }
      }
    }
  }
});
