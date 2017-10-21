import Controller from 'ember-controller';

export default Controller.extend({
  // Query Params
  queryParams: ['machineId', 'filterId', 'tabName'],

  machineId: null,

  filterId: null,

  tabName: null
});
