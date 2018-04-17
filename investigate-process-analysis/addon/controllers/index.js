import Controller from '@ember/controller';

export default Controller.extend({
  queryParams: [
    'pn', // process name
    'aid', // agent id
    'checksum',
    'st', // start time
    'et', // end time
    'sid' // metaPanelSize
  ]
});
