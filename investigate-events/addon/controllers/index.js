import Controller from '@ember/controller';
import { RECON_PANEL_SIZES } from 'investigate-events/constants/panelSizes';

export default Controller.extend({
  queryParams: [
    'sid', // serviceId
    'st',  // startTime
    'et',  // endTime
    'eid', // sessionId
    'mf',  // metaFilters
    'mps', // metaPanelSize
    'rs'   // reconSize
  ],

  actions: {
    controllerExecuteQuery() {
      this.send('executeQuery');
    },
    controllerMetaGroupKeyToggle(/* query */) {
      // TODO - Not implemented yet
      // this.send('metaGroupKeyToggle', query);
    },
    controllerMetaPanelSize(size) {
      this.send('metaPanelSize', size);
    },
    controllerNavDrill(/* query */) {
      // TODO - Not implemented yet
      // this.send('navDrill', query);
    },
    controllerReconClose() {
      this.send('reconClose');
    },
    controllerReconExpand() {
      this.send('reconSize', RECON_PANEL_SIZES.MAX);
    },
    controllerReconLinkToFile(file) {
      this.send('reconLinkToFile', file);
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