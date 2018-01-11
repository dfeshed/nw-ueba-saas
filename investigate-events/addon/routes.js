import buildRoutes from 'ember-engines/routes';

export default buildRoutes(function() {
  this.route('not-found', { path: '*invalidinvestigatepath' });
});
