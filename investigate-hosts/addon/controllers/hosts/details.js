import Controller from '@ember/controller';

export default Controller.extend({
  // Query Params
  queryParams: ['machineId', 'filterId', 'tabName', 'sid', 'subTabName', 'pid', 'mftFile'],

  machineId: null,

  filterId: null,

  tabName: null,

  sid: null,

  pid: null,

  subTabName: null,

  mftFile: null
});
