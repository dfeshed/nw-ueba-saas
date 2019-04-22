import { module, test } from 'qunit';
import Service from '@ember/service';
import { setupTest } from 'ember-qunit';
import { Promise } from 'rsvp';
import { next } from '@ember/runloop';
import { waitUntil } from '@ember/test-helpers';

// feature flag data looks like:
//
// {
//   code: 0,
//   data: {
//     'rsa.usm': true,
//     'rsa.usm.allowWindowsLogPolicyCreation,
//     'rsa.usm.viewSources,
//     'rsa.usm.featureTwo
//   }
// }

const sessionStorageClear = () => {
  sessionStorage.clear();
  return new Promise((resolve) => {
    waitUntil(() => sessionStorage.getItem('features.rsaUsm') === null).then(() => {
      next(null, resolve);
    });
  });
};

module('Unit | Route | protected', function(hooks) {
  setupTest(hooks);

  hooks.beforeEach(function() {
    this.owner.inject('route', 'request', 'service:request', 'service:features');
    return sessionStorageClear();
  });

  hooks.afterEach(function() {
    return sessionStorageClear();
  });

  test('should fetch Source Management (USM) feature flags and store in features service', async function(assert) {
    assert.expect(3);
    const features = this.owner.lookup('service:features');

    this.owner.register('service:-routing', Service.extend({
      currentRouteName: 'protected'
    }));
    const route = this.owner.lookup('route:protected');

    // disabled by default
    let isRsaUsmAllowWindowsLogPolicyCreationEnabled = features.isEnabled('rsa.usm.allowWindowsLogPolicyCreation');
    assert.equal(isRsaUsmAllowWindowsLogPolicyCreationEnabled, false, 'feature rsa.usm.allowWindowsLogPolicyCreation is disabled by default');

    const promise = route.getSourceManagementFeatures();
    await promise;

    // enabled by the service call
    isRsaUsmAllowWindowsLogPolicyCreationEnabled = features.isEnabled('rsa.usm.allowWindowsLogPolicyCreation');
    assert.equal(isRsaUsmAllowWindowsLogPolicyCreationEnabled, true, 'feature rsa.usm.allowWindowsLogPolicyCreation is enabled by the service call');

    // disabled for 11.4 for now
    const isRsaUsmViewSourcesEnabled = features.isEnabled('rsa.usm.viewSources');
    assert.equal(isRsaUsmViewSourcesEnabled, false, 'feature rsa.usm.viewSources is disabled by default');
  });

});
