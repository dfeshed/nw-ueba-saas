import buildRoutes from 'ember-engines/routes';

export default buildRoutes(function() {
  this.route('incidents');
  this.route('incident', { path: '/incident/:incident_id' });
  this.route('remediation');
  this.route('alerts');
  this.route('alert', { path: '/alert/:alert_id' });
  this.route('not-found', { path: '*invalidrespondpath' });
});