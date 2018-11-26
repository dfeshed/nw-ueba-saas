import Controller from '@ember/controller';

export default Controller.extend({
  // Query Params
  queryParams: [ 'checksum', 'sid' ],

  checksum: null,

  sid: null
});