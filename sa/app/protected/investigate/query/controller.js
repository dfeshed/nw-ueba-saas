import Ember from 'ember';

const { Controller } = Ember;

export default Controller.extend({
  queryParams: ['metaPanelSize'],
  metaPanelSize: 'default'
});
