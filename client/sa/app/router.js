import Ember from 'ember';
import config from './config/environment';

var Router = Ember.Router.extend({
  location: config.locationType
});

Router.map(function() {
  this.route('incidents');
  this.route('investigation');
  this.route('i18n');
});

export default Router;
