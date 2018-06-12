import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';

module('Unit | Controller | application', function(hooks) {
  setupTest(hooks, {
    resolver: engineResolverFor('admin')
  });

  hooks.beforeEach(function() {
    this.owner.inject('controller', 'service:accessControl', 'service:features');
  });

  // hooks.afterEach(function() {
  // });

  test('rsa.usm feature flag is false by default, so hasAdminViewUnifiedSourcesAccess should also be false', function(assert) {
    // 'rsa.usm' feature flag should be false by default
    const isRsaUsmEnabled = this.owner.lookup('service:features').isEnabled('rsa.usm');
    assert.equal(isRsaUsmEnabled, false, 'rsa.usm feature flag is false by default');

    // the viewUnifiedSources permission is already set to true by environment.js roles
    const hasViewUnifiedSources = this.owner.lookup('service:accessControl').get('hasAdminViewUnifiedSourcesAccess');
    assert.equal(hasViewUnifiedSources, true, 'viewUnifiedSources permission is true by default');

    // controller hasAdminViewUnifiedSourcesAccess computed prop should be false if either of the above are false
    const controller = this.owner.lookup('controller:application');
    const hasAdminViewUnifiedSourcesAccess = controller.get('hasAdminViewUnifiedSourcesAccess');
    assert.equal(hasAdminViewUnifiedSourcesAccess, false, 'hasAdminViewUnifiedSourcesAccess is false');
  });

  test('setting rsa.usm feature flag to true should also cause hasAdminViewUnifiedSourcesAccess to be true', function(assert) {
    // need to set feature flag to true since it is false by default, and is set externally by a parent app...
    this.owner.lookup('service:features').setFeatureFlags({ 'rsa.usm': true });

    // the viewUnifiedSources permission is already set to true by environment.js roles
    const hasViewUnifiedSources = this.owner.lookup('service:accessControl').get('hasAdminViewUnifiedSourcesAccess');
    assert.equal(hasViewUnifiedSources, true, 'viewUnifiedSources permission is true by default');

    // controller hasAdminViewUnifiedSourcesAccess computed prop should be true now since both of the above are true
    const controller = this.owner.lookup('controller:application');
    const hasAdminViewUnifiedSourcesAccess = controller.get('hasAdminViewUnifiedSourcesAccess');
    assert.equal(hasAdminViewUnifiedSourcesAccess, true, 'hasAdminViewUnifiedSourcesAccess is true');
  });
});
