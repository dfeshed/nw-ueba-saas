import Ember from 'ember';
import service from 'ember-service/inject';

const { Route } = Ember;

export default Route.extend({

  contextualHelp: service(),

  activate() {
    this.set('contextualHelp.module', this.get('contextualHelp.respondModule'));
  },

  deactivate() {
    this.set('contextualHelp.module', null);
  }

});
