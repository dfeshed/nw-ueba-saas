import buildRoutes from 'ember-engines/routes';

export default buildRoutes(function() {
  this.route('incidents');
  this.route('incident', { path: '/incident/:incident_id' }, function() {
    this.route('recon');
    this.route('ueba');
  });
  this.route('tasks');
  this.route('alerts');
  this.route('alert', { path: '/alert/:alert_id' });
  this.route('not-found', { path: '*invalidrespondpath' });
});
