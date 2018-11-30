import Controller from '@ember/controller';

export default Controller.extend({
  // Query Params
  queryParams: ['machineId', 'filterId', 'tabName', 'query', 'sid', 'subTabName', 'pid'],

  machineId: null,

  filterId: null,

  tabName: null,

  query: null,

  sid: null,

  pid: null,

  subTabName: null
});
