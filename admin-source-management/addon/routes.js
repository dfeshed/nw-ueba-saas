import buildRoutes from 'ember-engines/routes';

export default buildRoutes(function() {
  this.route('groups');
  this.route('group-create', { path: 'group/create' });
});
