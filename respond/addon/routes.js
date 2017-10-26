import buildRoutes from 'ember-engines/routes';

export default buildRoutes(function() {
  this.route('incidents');
  this.route('incident', { path: '/incident/:incident_id' });
  this.route('tasks');
  this.route('alerts');
  this.route('alert', { path: '/alert/:alert_id' });
  this.route('aggregation-rules');
  this.route('aggregation-rule-create', { path: '/aggregation-rule/create' });
  this.route('aggregation-rule', { path: '/aggregation-rule/:rule_id' });
  this.route('not-found', { path: '*invalidrespondpath' });
});