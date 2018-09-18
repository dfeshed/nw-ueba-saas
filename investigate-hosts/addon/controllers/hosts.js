import Controller from '@ember/controller';

export default Controller.extend({
  // Query Params
  queryParams: ['machineId', 'filterId', 'tabName', 'query', 'sid'],

  machineId: null,

  filterId: null,

  tabName: null,

  query: null,

  sid: null
});
