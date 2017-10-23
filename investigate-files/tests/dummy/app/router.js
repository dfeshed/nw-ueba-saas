import Ember from 'ember';
import config from './config/environment';

const { Router: ERouter } = Ember;

const Router = ERouter.extend({
  location: config.locationType,
  rootURL: config.rootURL
});

Router.map(function() {
  this.mount('investigate-files');
  this.route('events');
  this.route('hosts');
});

export default Router;
