import Controller from '@ember/controller';

export default Controller.extend({
  // Query Params
  queryParams: ['subTabName', 'checksum', 'scanTime', 'searchKey'],

  subTabName: null,

  scanTime: null,

  checksum: null,

  searchKey: null

});
