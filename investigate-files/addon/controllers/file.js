import Controller from '@ember/controller';

export default Controller.extend({
  // Query Params
  queryParams: [ 'checksum', 'sid', 'tabName', 'fileFormat' ],

  checksum: null,

  sid: null,

  tabName: null,

  fileFormat: null,

  actions: {
    controllerSwitchTabs(tabName, fileFormat) {
      this.send('switchToSelectedFileDetailsTab', tabName, fileFormat);
    }
  }
});