import Controller from '@ember/controller';

export default Controller.extend({
  // Query Params
  queryParams: ['machineId', 'filterId', 'tabName', 'sid', 'subTabName', 'pid', 'mftFile', 'mftName'],

  machineId: null,

  filterId: null,

  tabName: null,

  sid: null,

  pid: null,

  subTabName: null,

  mftFile: null,

  mftName: null
});
