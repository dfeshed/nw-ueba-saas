import Controller from '@ember/controller';
import { RECON_PANEL_SIZES } from 'investigate-events/constants/panelSizes';

export default Controller.extend({
  queryParams: [
    'eid', // sessionId
    'et', // endTime
    'mf', // pillData
    'mps', // metaPanelSize
    'pdhash', // pillData hashes
    'rs', // reconSize
    'sid', // serviceId
    'st', // startTime
    'sortField',
    'sortDir'
  ],

  actions: {
    controllerExecuteQuery(externalLink, sortField, sortDir) {
      this.send('executeQuery', externalLink, sortField, sortDir);
    },
    controllerMetaPanelSize(size) {
      this.send('metaPanelSize', size);
    },
    controllerReconClose() {
      this.send('reconClose');
    },
    controllerReconExpand() {
      this.send('reconSize', RECON_PANEL_SIZES.MAX);
    },
    controllerReconShrink() {
      this.send('reconSize', RECON_PANEL_SIZES.MIN);
    },
    controllerSelectEvent(event) {
      this.send('selectEvent', event);
    },
    controllerToggleReconSize() {
      this.send('toggleReconSize');
    },
    controllerToggleSlaveFullScreen() {
      this.send('toggleSlaveFullScreen');
    }
  }
});
