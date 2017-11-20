import Router from 'ember-router';
import config from './config/environment';

const DummyRouter = Router.extend({
  location: config.locationType,
  rootURL: config.rootURL
});

DummyRouter.map(function() {
  this.mount('investigate-events', { path: 'investigate/events' });
  this.route('not-found', { path: '*invalidinvestigatepath' });
  this.route('protected');
  this.route('files');
  this.route('hosts');
});

export default DummyRouter;
