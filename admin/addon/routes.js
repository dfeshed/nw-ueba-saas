import buildRoutes from 'ember-engines/routes';

export default buildRoutes(function() {
  this.route('usm', function() {
    this.route('groups');
  });
});
