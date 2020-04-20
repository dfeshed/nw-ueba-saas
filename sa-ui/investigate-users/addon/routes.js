import buildRoutes from 'ember-engines/routes';

export default buildRoutes(function() {
  this.route('entities', { path: '/entities' });
  this.route('alerts', { path: '/alerts' });
  this.route('permission-denied');
});
