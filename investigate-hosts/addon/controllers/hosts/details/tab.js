import Controller from '@ember/controller';

export default Controller.extend({
  // Query Params
  queryParams: ['subTabName', 'checksum', 'scanTime'],

  subTabName: null,

  scanTime: null,

  checksum: null

});
