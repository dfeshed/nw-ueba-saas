import buildRoutes from 'ember-engines/routes';

export default buildRoutes(function() {
  this.route('hosts', { path: '/' });
  this.route('permission-denied');
});
