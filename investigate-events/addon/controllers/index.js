import Controller from '@ember/controller';
import { RECON_PANEL_SIZES } from 'investigate-events/constants/panelSizes';

export default Controller.extend({
  queryParams: [
    'sid', // serviceId
    'st', // startTime
    'et', // endTime
    'eid', // sessionId
    'mf', // pillData
    'mps', // metaPanelSize
    'rs' // reconSize
  ],

  actions: {
    controllerExecuteQuery(externalLink) {
      this.send('executeQuery', externalLink);
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
