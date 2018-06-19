import Controller from '@ember/controller';

export default Controller.extend({
  queryParams: [
    'pn', // process name
    'aid', // agent id
    'checksum',
    'st', // start time
    'et', // end time
    'sid',
    'osType', // metaPanelSize
    'vid', // Process identification
    'hn' // Host name
  ],
  actions: {
    controllerExecuteQuery() {
      this.send('executeQuery');
    }
  }
});
