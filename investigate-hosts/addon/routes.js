import buildRoutes from 'ember-engines/routes';

export default buildRoutes(function() {
  this.route('permission-denied');
  this.route('hosts', { path: '/' }, function() {
    this.route('details', { path: '/:id' });
  });
});
