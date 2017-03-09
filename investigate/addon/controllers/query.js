import Controller from 'ember-controller';

export default Controller.extend({
  queryParams: ['eventId', 'metaPanelSize', 'reconSize'],
  eventId: -1,
  metaPanelSize: 'default',
  reconSize: 'max'
});
