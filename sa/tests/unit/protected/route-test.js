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
//     'rsa.usm.viewSourcesFeature': false,
//     'rsa.usm.filePolicyFeature': true,
//     'rsa.usm.allowFilePolicies': true
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
    assert.expect(6);
    const features = this.owner.lookup('service:features');

    this.owner.register('service:-routing', Service.extend({
      currentRouteName: 'protected'
    }));
    const route = this.owner.lookup('route:protected');

    // everything is disabled by default before being enabled by the service call
    let isRsaUsmViewSourcesFeatureEnabled = features.isEnabled('rsa.usm.viewSourcesFeature');
    assert.equal(isRsaUsmViewSourcesFeatureEnabled, false, 'feature rsa.usm.viewSourcesFeature is disabled by default');
    let isRsaUsmFilePolicyFeatureEnabled = features.isEnabled('rsa.usm.filePolicyFeature');
    assert.equal(isRsaUsmFilePolicyFeatureEnabled, false, 'feature rsa.usm.filePolicyFeature is disabled by default');
    let isRsaUsmAllowFilePoliciesEnabled = features.isEnabled('rsa.usm.allowFilePolicies');
    assert.equal(isRsaUsmAllowFilePoliciesEnabled, false, 'feature rsa.usm.allowFilePolicies is disabled by default');

    const promise = route.getSourceManagementFeatures();
    await promise;

    // disabled for 11.4 for now
    isRsaUsmViewSourcesFeatureEnabled = features.isEnabled('rsa.usm.viewSourcesFeature');
    assert.equal(isRsaUsmViewSourcesFeatureEnabled, false, 'feature rsa.usm.viewSourcesFeature is disabled disabled for 11.4 for now');

    // enabled by the service call
    isRsaUsmFilePolicyFeatureEnabled = features.isEnabled('rsa.usm.filePolicyFeature');
    assert.equal(isRsaUsmFilePolicyFeatureEnabled, true, 'feature rsa.usm.filePolicyFeature is enabled by the service call');
    isRsaUsmAllowFilePoliciesEnabled = features.isEnabled('rsa.usm.allowFilePolicies');
    assert.equal(isRsaUsmAllowFilePoliciesEnabled, true, 'feature rsa.usm.allowFilePolicies is enabled by the service call');
  });

});
