import buildRoutes from 'ember-engines/routes';

export default buildRoutes(function() {
  this.route('index');
  this.route('live', function() {
    this.route('search');
    this.route('deployed');
    this.route('updates');
    this.route('jobs');
    this.route('feeds');
    this.route('custom');
    this.route('home');
  });
});