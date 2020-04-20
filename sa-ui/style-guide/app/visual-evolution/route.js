import fetch from 'component-lib/utils/fetch';
import Route from '@ember/routing/route';
import config from 'ember-get-config';

export default Route.extend({

  activate() {
    this.controllerFor('application').set('hideAppChrome', true);
  },

  deactivate() {
    this.controllerFor('application').set('hideAppChrome', false);
  },

  model() {
    return fetch(`${config.visualTourRootUrl}/storage.json`);
  }
});
