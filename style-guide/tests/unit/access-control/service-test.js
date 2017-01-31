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
  service.get('roles').clear().addObject('accessInvestigationModule');
  assert.equal(service.get('hasInvestigateAccess'), true);
});

test('hasRespondAccess is set when required roles are included', function(assert) {
  const service = this.subject();
  assert.equal(service.get('hasRespondAccess'), false);
  service.get('roles').clear().addObject('accessIncidentModule');
  assert.equal(service.get('hasRespondAccess'), true);
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
  service.get('roles').clear().addObject('accessManageAlertHandlingRules');
  assert.equal(service.get('hasConfigAccess'), true);
  service.get('roles').clear().addObject('accessViewRules');
  assert.equal(service.get('hasConfigAccess'), true);
  service.get('roles').clear().addObject('manageLiveResources');
  assert.equal(service.get('hasConfigAccess'), true);
  service.get('roles').clear().addObject('manageLiveFeeds');
  assert.equal(service.get('hasConfigAccess'), true);
});
