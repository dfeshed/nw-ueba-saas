import { moduleFor, test } from 'ember-qunit';

moduleFor('service:access-control', 'Unit | Service | access control');

test('hasMonitorAccess is true by default', function(assert) {
  const service = this.subject();
  service.set('roles', []);
  assert.equal(service.get('hasMonitorAccess'), true);
});

test('hasInvestigateAccess is set when required roles are included', function(assert) {
  const service = this.subject();
  assert.equal(service.get('hasInvestigateAccess'), false);
  service.get('roles').clear().addObject('*');
  assert.equal(service.get('hasInvestigateAccess'), true);
  service.get('roles').clear();

  assert.equal(service.get('hasInvestigateAccess'), false);
  service.get('roles').clear().addObject('accessInvestigationModule');
  assert.equal(service.get('hasInvestigateAccess'), true);
  service.get('roles').clear();

  assert.equal(service.get('hasInvestigateAccess'), false);
  service.get('roles').clear().addObject('investigate-server.configuration.manage');
  assert.equal(service.get('hasInvestigateAccess'), true);
  service.get('roles').clear();
});

test('hasInvestigateClassicAccess is set when required roles are included', function(assert) {
  const service = this.subject();
  assert.equal(service.get('hasInvestigateClassicAccess'), false);
  service.get('roles').clear().addObject('*');
  assert.equal(service.get('hasInvestigateClassicAccess'), true);
  service.get('roles').clear();

  assert.equal(service.get('hasInvestigateClassicAccess'), false);
  service.get('roles').clear().addObject('accessInvestigationModule');
  assert.equal(service.get('hasInvestigateClassicAccess'), true);
  service.get('roles').clear();

  assert.equal(service.get('hasInvestigateClassicAccess'), false);
  service.get('roles').clear().addObject('manageContextList');
  assert.equal(service.get('hasInvestigateClassicAccess'), true);
  service.get('roles').clear();

  assert.equal(service.get('hasInvestigateClassicAccess'), false);
  service.get('roles').clear().addObject('contextLookup');
  assert.equal(service.get('hasInvestigateClassicAccess'), true);
  service.get('roles').clear();

  assert.equal(service.get('hasInvestigateClassicAccess'), false);
  service.get('roles').clear().addObject('navigateDevices');
  assert.equal(service.get('hasInvestigateClassicAccess'), true);
  service.get('roles').clear();

  assert.equal(service.get('hasInvestigateClassicAccess'), false);
  service.get('roles').clear().addObject('navigateCreateIncidents');
  assert.equal(service.get('hasInvestigateClassicAccess'), true);
  service.get('roles').clear();

  assert.equal(service.get('hasInvestigateClassicAccess'), false);
  service.get('roles').clear().addObject('navigateEvents');
  assert.equal(service.get('hasInvestigateClassicAccess'), true);
  service.get('roles').clear();
});

test('hasInvestigateEmberAccess is set when required roles are included', function(assert) {
  const service = this.subject();
  assert.equal(service.get('hasInvestigateEmberAccess'), false);
  service.get('roles').clear().addObject('*');
  assert.equal(service.get('hasInvestigateEmberAccess'), true);
  service.get('roles').clear();

  assert.equal(service.get('hasInvestigateEmberAccess'), false);
  service.get('roles').clear().addObject('investigate-server.configuration.manage');
  assert.equal(service.get('hasInvestigateEmberAccess'), true);
  service.get('roles').clear();

  assert.equal(service.get('hasInvestigateEmberAccess'), false);
  service.get('roles').clear().addObject('investigate-server.logs.manage');
  assert.equal(service.get('hasInvestigateEmberAccess'), true);
  service.get('roles').clear();

  assert.equal(service.get('hasInvestigateEmberAccess'), false);
  service.get('roles').clear().addObject('investigate-server.security.read');
  assert.equal(service.get('hasInvestigateEmberAccess'), true);
  service.get('roles').clear();

  assert.equal(service.get('hasInvestigateEmberAccess'), false);
  service.get('roles').clear().addObject('investigate-server.process.manage');
  assert.equal(service.get('hasInvestigateEmberAccess'), true);
  service.get('roles').clear();

  assert.equal(service.get('hasInvestigateEmberAccess'), false);
  service.get('roles').clear().addObject('investigate-server.health.read');
  assert.equal(service.get('hasInvestigateEmberAccess'), true);
  service.get('roles').clear();

  assert.equal(service.get('hasInvestigateEmberAccess'), false);
  service.get('roles').clear().addObject('investigate-server.*');
  assert.equal(service.get('hasInvestigateEmberAccess'), true);
  service.get('roles').clear();

  assert.equal(service.get('hasInvestigateEmberAccess'), false);
  service.get('roles').clear().addObject('investigate-server.security.manage');
  assert.equal(service.get('hasInvestigateEmberAccess'), true);
  service.get('roles').clear();

  assert.equal(service.get('hasInvestigateEmberAccess'), false);
  service.get('roles').clear().addObject('investigate-server.metrics.read');
  assert.equal(service.get('hasInvestigateEmberAccess'), true);
  service.get('roles').clear();

  assert.equal(service.get('hasInvestigateEmberAccess'), false);
  service.get('roles').clear().addObject('investigate-server.event.read');
  assert.equal(service.get('hasInvestigateEmberAccess'), true);
  service.get('roles').clear();

  assert.equal(service.get('hasInvestigateEmberAccess'), false);
  service.get('roles').clear().addObject('investigate-server.content.export');
  assert.equal(service.get('hasInvestigateEmberAccess'), true);
  service.get('roles').clear();

  assert.equal(service.get('hasInvestigateEmberAccess'), false);
  service.get('roles').clear().addObject('investigate-server.content.reconstruct');
  assert.equal(service.get('hasInvestigateEmberAccess'), true);
  service.get('roles').clear();
});

test('hasInvestigateContentExportAccess is set when required roles are included', function(assert) {
  const service = this.subject();
  assert.equal(service.get('hasInvestigateContentExportAccess'), false);
  service.set('roles', ['investigate-server.content.export']);
  assert.equal(service.get('hasInvestigateContentExportAccess'), true);
});

test('hasReconAccess is set when required roles are included', function(assert) {
  const service = this.subject();
  assert.equal(service.get('hasReconAccess'), false);
  service.set('roles', ['investigate-server.content.reconstruct']);
  assert.equal(service.get('hasReconAccess'), true);
});

test('hasInvestigateEventsAccess is set when required roles are included', function(assert) {
  const service = this.subject();
  assert.equal(service.get('hasInvestigateEventsAccess'), false);
  service.set('roles', ['investigate-server.event.read']);
  assert.equal(service.get('hasInvestigateEventsAccess'), true);
});

test('hasRespondAccess is set when required roles are included', function(assert) {
  const service = this.subject();
  assert.equal(service.get('hasRespondAccess'), false);
  service.set('roles', ['respond-server.*']);
  assert.equal(service.get('hasRespondAccess'), true);
});

test('hasRespondAlertsAccess is set when required roles are included', function(assert) {
  const service = this.subject();
  assert.equal(service.get('hasRespondAlertsAccess'), false);
  service.set('roles', ['respond-server.alert.read', 'respond-server.alert.manage']);
  assert.equal(service.get('hasRespondAlertsAccess'), true);
});

test('hasRespondIncidentsAccess is set when required roles are included', function(assert) {
  const service = this.subject();
  assert.equal(service.get('hasRespondIncidentsAccess'), false);
  service.set('roles', ['respond-server.incident.read', 'respond-server.incident.manage']);
  assert.equal(service.get('hasRespondIncidentsAccess'), true);
});

test('hasRespondRemediationAccess is set when required roles are included', function(assert) {
  const service = this.subject();
  assert.equal(service.get('hasRespondRemediationAccess'), false);
  service.set('roles', ['respond-server.remediation.read', 'respond-server.remediation.manage']);
  assert.equal(service.get('hasRespondRemediationAccess'), true);
});

test('hasRespondAlertRulesAccess is set when required roles are included', function(assert) {
  const service = this.subject();
  service.set('roles', []);
  assert.equal(service.get('hasRespondAlertRulesAccess'), false);
  service.set('roles', ['respond-server.alertrule.read']);
  assert.equal(service.get('hasRespondAlertRulesAccess'), true);
  service.set('roles', ['respond-server.alertrule.manage']);
  assert.equal(service.get('hasRespondAlertRulesAccess'), true);
});

test('respondCanManageAlertRules is set when required roles are included', function(assert) {
  const service = this.subject();
  service.set('roles', []);
  assert.equal(service.get('respondCanManageAlertRules'), false);
  service.set('roles', ['respond-server.alertrule.manage']);
  assert.equal(service.get('respondCanManageAlertRules'), true);
});

test('hasRespondNotificationsAccess is set when required roles are included', function(assert) {
  const service = this.subject();
  service.set('roles', []);
  assert.equal(service.get('hasRespondNotificationsAccess'), false);
  service.set('roles', ['respond-server.notification.read']);
  assert.equal(service.get('hasRespondNotificationsAccess'), false);
  service.set('roles', ['integration-server.notification.read', 'respond-server.notification.read']);
  assert.equal(service.get('hasRespondNotificationsAccess'), true);
});

test('respondCanManageNotifications is set when required roles are included', function(assert) {
  const service = this.subject();
  service.set('roles', []);
  assert.equal(service.get('respondCanManageNotifications'), false);
  service.set('roles', ['respond-server.notification.manage']);
  assert.equal(service.get('respondCanManageNotifications'), false);
  service.set('roles', ['integration-server.notification.manage', 'respond-server.notification.manage']);
  assert.equal(service.get('respondCanManageNotifications'), true);
});

test('hasAdminAccess is set when required roles are included', function(assert) {
  const service = this.subject();
  assert.equal(service.get('hasAdminAccess'), false);
  service.get('roles').clear().addObject('viewAppliances');
  assert.equal(service.get('hasAdminAccess'), true);
  service.get('roles').clear().addObject('viewServices');
  assert.equal(service.get('hasAdminAccess'), true);
  service.get('roles').clear().addObject('viewEventSources');
  assert.equal(service.get('hasAdminAccess'), true);
  service.get('roles').clear().addObject('accessHealthWellness');
  assert.equal(service.get('hasAdminAccess'), true);
  service.get('roles').clear().addObject('manageSystemSettings');
  assert.equal(service.get('hasAdminAccess'), true);
  service.get('roles').clear().addObject('manageSASecurity');
  assert.equal(service.get('hasAdminAccess'), true);
});

test('hasConfigAccess is set when required roles are included', function(assert) {
  const service = this.subject();
  assert.equal(service.get('hasConfigAccess'), false);
  service.get('roles').clear().addObject('searchLiveResources');
  assert.equal(service.get('hasConfigAccess'), true);
  assert.equal(service.get('hasLiveSearchAccess'), true);
  service.get('roles').clear().addObject('accessManageAlertHandlingRules');
  assert.equal(service.get('hasConfigAccess'), true);
  service.get('roles').clear().addObject('accessViewRules');
  assert.equal(service.get('hasESARulesAccess'), true);
  assert.equal(service.get('hasConfigAccess'), true);
  service.get('roles').clear().addObject('manageLiveResources');
  assert.equal(service.get('hasConfigAccess'), true);
  assert.equal(service.get('hasLiveResourcesAccess'), true);
  service.get('roles').clear().addObject('manageLiveFeeds');
  assert.equal(service.get('hasConfigAccess'), true);
  assert.equal(service.get('hasLiveFeedsAccess'), true);
});