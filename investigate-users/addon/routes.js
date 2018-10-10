import buildRoutes from 'ember-engines/routes';

export default buildRoutes(function() {
  this.route('users', { path: '/users' });
  this.route('alerts', { path: '/alerts' });
  this.route('permission-denied');
});
