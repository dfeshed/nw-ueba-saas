import Ember from 'ember';
import config from './config/environment';

var Router = Ember.Router.extend({
    location: config.locationType
});

Router.map(function() {
  this.route('incidents');
  this.route('explorer');
  this.route('login');
  this.route('about');
});

export default Router;
