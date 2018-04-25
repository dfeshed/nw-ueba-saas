import Controller from '@ember/controller';

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

    // let router handle this
    controllerReconClose() {
      this.send('reconClose');
    }
  }
});