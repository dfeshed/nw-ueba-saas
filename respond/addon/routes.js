import buildRoutes from 'ember-engines/routes';

export default buildRoutes(function() {
  this.route('incidents');
  this.route('incident', { path: '/incident/:incident_id' });
  this.route('remediation');
  this.route('alerts');
  this.route('not-found', { path: '*invalidrespondpath' });
});