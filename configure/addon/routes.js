import buildRoutes from 'ember-engines/routes';

export default buildRoutes(function() {
  this.route('respond', function() {
    this.route('notifications');
    this.route('incident-rules');
    this.route('incident-rule-create', { path: 'incident-rule/create' });
    this.route('incident-rule', { path: 'incident-rule/:rule_id' });
  });
  this.route('content', function() {
    this.route('log-parser');
  });
  this.route('hosts-scan');
  this.route('not-found', { path: '*invalidconfigurepath' });
});
