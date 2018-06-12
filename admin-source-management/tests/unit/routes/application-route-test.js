import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';
import sinon from 'sinon';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';

module('Unit | Route | application', function(hooks) {
  setupTest(hooks, {
    resolver: engineResolverFor('admin-source-management')
  });

  hooks.beforeEach(function() {
    this.owner.inject('route', 'service:accessControl', 'service:features');
  });

  // hooks.afterEach(function() {
  // });

  test('rsa.usm feature flag is false by default, so it should transitionToExternal protected route', function(assert) {
    // 'rsa.usm' feature flag should be false by default
    const isRsaUsmEnabled = this.owner.lookup('service:features').isEnabled('rsa.usm');
    assert.equal(isRsaUsmEnabled, false, 'rsa.usm feature flag is false by default');

    // the viewUnifiedSources permission is already set to true by environment.js roles
    const hasViewUnifiedSources = this.owner.lookup('service:accessControl').get('hasAdminViewUnifiedSourcesAccess');
    assert.equal(hasViewUnifiedSources, true, 'viewUnifiedSources permission is true by default');

    // router should transitionToExternal to 'protected' if either of the above are false
    const routeSpy = sinon.spy();
    const route = this.owner.lookup('route:application');
    route.set('router.currentRouteName', 'application');
    route.transitionToExternal = routeSpy;
    route.beforeModel();
    assert.ok(routeSpy.calledOnce, 'transitionToExternal was called once');
    assert.ok(routeSpy.calledWith('protected'), 'transitionToExternal was called with protected');
  });

  test('setting rsa.usm feature flag to true should prevent transitionToExternal protected route', function(assert) {
    // need to set feature flag to true since it is false by default, and is set externally by a parent app...
    this.owner.lookup('service:features').setFeatureFlags({ 'rsa.usm': true });

    // the viewUnifiedSources permission is already set to true by environment.js roles
    const hasViewUnifiedSources = this.owner.lookup('service:accessControl').get('hasAdminViewUnifiedSourcesAccess');
    assert.equal(hasViewUnifiedSources, true, 'viewUnifiedSources permission is true by default');

    // router should NOT transitionToExternal to 'protected' since both of the above are true
    const routeSpy = sinon.spy();
    const route = this.owner.lookup('route:application');
    route.set('router.currentRouteName', 'application');
    route.transitionToExternal = routeSpy;
    route.beforeModel();
    assert.ok(routeSpy.notCalled, 'transitionToExternal was NOT called');
  });
});