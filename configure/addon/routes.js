import buildRoutes from 'ember-engines/routes';

export default buildRoutes(function() {
  this.route('respond', function() {
    this.route('incident-rules');
    this.route('incident-rule-create', { path: 'incident-rule/create' });
    this.route('incident-rule', { path: 'incident-rule/:rule_id' });
  });
  this.route('not-found', { path: '*invalidconfigurepath' });
});