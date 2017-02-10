import Ember from 'ember';

const { Controller } = Ember;

export default Controller.extend({
  queryParams: ['entityType'],
  entityType: 'IP'
});
