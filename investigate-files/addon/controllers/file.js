import Controller from '@ember/controller';

export default Controller.extend({
  // Query Params
  queryParams: [ 'checksum', 'serverId' ],

  checksum: null,

  serverId: null
});