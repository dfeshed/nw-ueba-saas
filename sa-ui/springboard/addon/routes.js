import buildRoutes from 'ember-engines/routes';

export default buildRoutes(function() {
  this.route('permission-denied');
  this.route('springboard', { path: '/' }, function() {
    this.route('show', { path: '/:id' });
  });
});
