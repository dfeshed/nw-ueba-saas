import buildRoutes from 'ember-engines/routes';

export default buildRoutes(function() {
  this.route('query', { path: 'query/*filter' });
  this.route('permission-denied');
  this.route('not-found', { path: '*invalidinvestigatepath' });
});
