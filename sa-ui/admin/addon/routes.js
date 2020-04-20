import buildRoutes from 'ember-engines/routes';

export default buildRoutes(function() {
  this.mount('admin-source-management', { path: '/usm' });
  // this.mount('admin-other-engine-2', { path: '/engine2PrettyPath' });
  // this.mount('admin-other-engine-3', { path: '/engine3PrettyPath' });
});
