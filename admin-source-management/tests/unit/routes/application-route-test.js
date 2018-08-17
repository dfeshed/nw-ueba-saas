import { module, test } from 'qunit';
import Service from '@ember/service';
import { setupTest } from 'ember-qunit';
import { computed } from '@ember/object';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import ApplicationRoute from 'admin-source-management/routes/application';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';

let transitionToExternal, hasPermission;

module('Unit | Route | application', function(hooks) {
  setupTest(hooks, {
    resolver: engineResolverFor('admin-source-management')
  });

  hooks.beforeEach(function() {
    hasPermission = true;
    transitionToExternal = null;
    initialize(this.owner);
  });

  const setupRoute = function() {
    this.owner.register('service:-routing', Service.extend({
      currentRouteName: 'application'
    }));
    const accessControl = Service.extend({
      hasAdminViewUnifiedSourcesAccess: computed(function() {
        return hasPermission;
      })
    }).create();
    const features = this.owner.lookup('service:features');
    const PatchedRoute = ApplicationRoute.extend({
      accessControl: computed(function() {
        return accessControl;
      }),
      features: computed(function() {
        return features;
      }),
      transitionToExternal(routeName) {
        transitionToExternal = routeName;
      }
    });
    return PatchedRoute.create();
  };

  test('rsa.usm feature flag is false by default, so it should transitionToExternal protected route', async function(assert) {
    const route = setupRoute.call(this);

    // 'rsa.usm' feature flag should be false by default
    const isRsaUsmEnabled = this.owner.lookup('service:features').isEnabled('rsa.usm');
    assert.equal(isRsaUsmEnabled, false, 'rsa.usm feature flag is false by default');

    await route.beforeModel();

    assert.equal(transitionToExternal, 'protected', 'transitionToExternal was called with protected');
  });

  test('setting rsa.usm feature flag to true should prevent transitionToExternal protected route', async function(assert) {
    const route = setupRoute.call(this);

    // need to set feature flag to true since it is false by default, and is set externally by a parent app...
    this.owner.lookup('service:features').setFeatureFlags({ 'rsa.usm': true });

    await route.beforeModel();

    assert.equal(transitionToExternal, null, 'transitionToExternal was NOT called');
  });
});
