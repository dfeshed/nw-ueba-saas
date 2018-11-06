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

  test('hasAdminViewUnifiedSourcesAccess should be true', function(assert) {
    // the viewUnifiedSources permission is already set to true by environment.js roles
    const hasViewUnifiedSources = this.owner.lookup('service:accessControl').get('hasAdminViewUnifiedSourcesAccess');
    assert.equal(hasViewUnifiedSources, true, 'viewUnifiedSources permission is true by default');

    // controller hasAdminViewUnifiedSourcesAccess computed prop should be true
    const controller = this.owner.lookup('controller:application');
    const hasAdminViewUnifiedSourcesAccess = controller.get('hasAdminViewUnifiedSourcesAccess');
    assert.equal(hasAdminViewUnifiedSourcesAccess, true, 'hasAdminViewUnifiedSourcesAccess is true');
  });

});
