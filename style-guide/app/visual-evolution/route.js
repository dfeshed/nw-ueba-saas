import $ from 'jquery';
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
    return $.getJSON(`${config.visualTourRootUrl}/storage.json`);
  }
});
