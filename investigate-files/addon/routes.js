import buildRoutes from 'ember-engines/routes';

export default buildRoutes(function() {
  this.route('permission-denied');
  this.route('file', { path: '/file' });
});