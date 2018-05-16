import buildRoutes from 'ember-engines/routes';

export default buildRoutes(function() {
  this.mount('admin-source-management', { path: '/usm' });
});
