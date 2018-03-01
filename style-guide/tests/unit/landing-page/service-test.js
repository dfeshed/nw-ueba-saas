import EmObj from '@ember/object';
import { moduleFor, test } from 'ember-qunit';

moduleFor('service:landing-page', 'Unit | Service | landing page', {
  // Specify the other units that are required for this test.
  needs: ['service:accessControl', 'service:request', 'service:flashMessages', 'service:i18n']
});

// Replace this with your real tests.
test('it exists', function(assert) {
  const service = this.subject();
  assert.ok(service);
});

test('it provides the correct options', function(assert) {
  const accessControl = EmObj.create({
    adminUrl: '/admin',
    configUrl: '/config',
    hasAdminAccess: true,
    hasConfigAccess: true,
    hasMonitorAccess: true,
    hasInvestigateAccess: true,
    hasRespondAccess: true
  });
  const service = this.subject({ accessControl });
  assert.equal(service.get('options.length'), 5);
  const options = service.get('options').map(function(option) {
    return option.key;
  });
  assert.ok(options.includes('/respond'));
  assert.ok(options.includes('/unified'));
  assert.ok(options.includes('/investigate'));
  assert.ok(options.includes(service.get('accessControl.adminUrl')));
  assert.ok(options.includes(service.get('accessControl.configUrl')));
  // TODO: uncomment when we ship the new investigate landing page
  // assert.ok(options.includes('/investigate'));
});
